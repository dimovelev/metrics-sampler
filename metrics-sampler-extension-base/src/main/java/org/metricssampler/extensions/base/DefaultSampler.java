package org.metricssampler.extensions.base;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.values.ValueTransformer;
import org.metricssampler.writer.MetricWriteException;
import org.metricssampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSampler implements Sampler {
	private final Logger logger;
	private final Logger timingsLogger;

	private final Random random = new Random(System.currentTimeMillis());

	private final DefaultSamplerConfig config;
	private final MetricsReader reader;
	private final List<MetricsWriter> writers = new LinkedList<MetricsWriter>();
	private final List<MetricsSelector> selectors = new LinkedList<MetricsSelector>();
	private final List<ValueTransformer> valueTransformers = new LinkedList<ValueTransformer>();
	
	private final Map<String, Object> variables;

	/**
	 * close will actually disconnect even persistent connections if the current timestamp >= this value. Long.MAX_VALUE effectively disables
	 * that.
	 */
	private long resetAfterTimestamp = Long.MAX_VALUE;

	/**
	 * The number of matched metric names (stored in metadata) after the last successful connection
	 */
	private int prevNumberOfSelectedMetrics = 0;

	public DefaultSampler(final DefaultSamplerConfig config, final MetricsReader reader) {
		checkArgumentNotNull(config, "config");
		checkArgumentNotNull(reader, "reader");
		this.config = config;
		this.reader = reader;
		this.variables = prepareVariables();
		logger = LoggerFactory.getLogger("sampler." + this.config.getName());
		timingsLogger = LoggerFactory.getLogger("timings.sampler");
	}

	private Map<String, Object> prepareVariables() {
		final Map<String, Object> result = new HashMap<>();
		result.putAll(config.getGlobalVariables());
		result.putAll(reader.getVariables());
		result.putAll(config.getVariables());
		result.put("sampler.name", config.getName());
		result.put("sampler.interval", config.getInterval());
		return Collections.unmodifiableMap(result);
	}

	public DefaultSampler addWriter(final MetricsWriter writer) {
		checkArgumentNotNull(writer, "writer");
		writers.add(writer);
		return this;
	}

	public DefaultSampler addSelector(final MetricsSelector selector) {
		checkArgumentNotNull(selector, "selector");
		selectors.add(selector);
		selector.setVariables(variables);
		return this;
	}
	
	public DefaultSampler addValueTransformer(final ValueTransformer valueTransformer) {
		checkArgumentNotNull(valueTransformer, "valueTransformer");
		valueTransformers.add(valueTransformer);
		return this;
	}

	protected void openWriters() {
		for (final MetricsWriter writer : writers) {
			writer.open();
		}
	}

	protected void closeWriters() {
		for (final MetricsWriter writer : writers) {
			writer.close();
		}
	}

	@Override
	public void sample() {
		logger.debug("Sampling");
		try {
			final long readStart = System.currentTimeMillis();
			final Map<String, MetricValue> rawMetrics = readMetrics();
			final Map<String, MetricValue> metrics = transformValues(rawMetrics);
			final long readEnd = System.currentTimeMillis();
			timingsLogger.debug("Sampled {} metrics in {} ms", metrics.size(), readEnd-readStart);
			writeMetrics(metrics);
			timingsLogger.debug("Metrics sent to writers in {} ms", System.currentTimeMillis()-readEnd);
			SamplerStats.get().setMetricsCount(metrics.size());
		} catch (final OpenMetricsReaderException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to open reader", e);
			} else {
				if (!config.isQuiet()) {
					final String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
					logger.info("Failed to open reader: {}", msg);
				}
			}
		} catch (final MetricReadException e) {
			logger.warn("Failed to read metrics", e);
		} catch (final MetricWriteException e) {
			logger.warn("Failed to write metrics", e);
		}
	}

	protected Map<String, MetricValue> transformValues(final Map<String, MetricValue> metrics) {
		if (valueTransformers.isEmpty()) {
			return metrics;
		} else {
			logger.debug("Transforming values");
			final Map<String, MetricValue> result = new HashMap<>();
			for (final Entry<String, MetricValue> entry : metrics.entrySet()) {
				final MetricValue newValue = transformValue(entry.getKey(), entry.getValue());
				result.put(entry.getKey(), newValue);
			}
			return result;
		}
	}

	protected MetricValue transformValue(final String name, final MetricValue value) {
		for (final ValueTransformer transformer : valueTransformers) {
			if (transformer.matches(name)) {
				final String newValue = transformer.transform(value.getValue().toString());
				return new MetricValue(value.getTimestamp(), newValue);
			}
		}
		return value;
	}

	private void writeMetrics(final Map<String, MetricValue> metrics) {
		openWriters();

		for (final MetricsWriter writer : writers) {
			try {
				logger.debug("Writing metrics to " + writer);
				writer.write(metrics);
			} catch(final MetricWriteException e) {
				logger.warn("Failed to write metrics to "+writer);
			}
		}

		closeWriters();
	}

	private Map<String, MetricValue> readMetrics() {
		logger.debug("Opening reader {}", reader);
		reader.open();

		logger.debug("Reading metrics from {}", reader);
		final Map<String, MetricValue> result = new HashMap<>();
		for (final MetricsSelector selector : selectors) {
			logger.debug("Reading metrics from {} via {}", reader, selector);
			final Map<String, MetricValue> metrics = selector.readMetrics(reader);
			logger.debug("Selector " + selector + " returned " + metrics.size() + " metrics for " + reader);
			result.putAll(metrics);
		}

		reader.close();

		scheduleResetIfNecessary(result.size());

		if (System.currentTimeMillis() >= resetAfterTimestamp) {
			reset();
		}

		return result;
	}

	protected void scheduleResetIfNecessary(final int newNumberOfSelectedMetrics) {
		final boolean noMetricsSelected = newNumberOfSelectedMetrics == 0;
		if (noMetricsSelected) {
			logger.warn("No metrics selected. Scheduling immediate reset so that metrics are selected again next time.");
			resetAfterTimestamp = Long.MIN_VALUE;
		} else if (config.getResetTimeout() > 0 && prevNumberOfSelectedMetrics != newNumberOfSelectedMetrics) {
			// there is a difference between the metrics we matched this time and the ones we matched last time
			// => reconnect after a timeout
			if (resetAfterTimestamp == Long.MAX_VALUE) {
				final int timeout = computeRandomResetTimeoutMs();
				logger.info("Scheduling reset after {} ms to reload the selected metrics as their count differs from the last time. The delta (new-old) is {}", timeout, newNumberOfSelectedMetrics-prevNumberOfSelectedMetrics);
				prevNumberOfSelectedMetrics = newNumberOfSelectedMetrics;
				resetAfterTimestamp = System.currentTimeMillis() + timeout;
			} else {
				logger.debug("Reset already scheduled");
			}
		}
	}

	protected int computeRandomResetTimeoutMs() {
		final int min = (int) (config.getResetTimeout()*1000*0.8f);
		final int max = (int) (config.getResetTimeout()*1000*1.2f);
		return min + random.nextInt(max-min);
	}

	@Override
	public boolean check() {
		boolean result = true;
		reader.open();

		for (final MetricsSelector transformer : selectors) {
			final int count = transformer.getMetricCount(this.reader);
			if (count == 0) {
				System.out.println(transformer + " has no metrics");
				result = false;
			} else {
				System.out.println(transformer + " matches " + count + " metrics");
			}
		}

		reader.close();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + reader + "->" + writers + "]";
	}

	@Override
	public DefaultSamplerConfig getConfig() {
		return config;
	}

	@Override
	public void reset() {
		logger.info("Resetting");
		resetAfterTimestamp = Long.MAX_VALUE;
		reader.reset();
		for (final MetricsSelector selector : selectors) {
			selector.reset();
		}
	}
}
