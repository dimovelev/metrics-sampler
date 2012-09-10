package org.jmxsampler.extensions.base.sampler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmxsampler.config.Placeholder;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.selector.MetricsSelector;
import org.jmxsampler.writer.MetricWriteException;
import org.jmxsampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSampler implements Sampler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MetricsReader reader;
	private final List<MetricsWriter> writers = new LinkedList<MetricsWriter>();
	private final List<MetricsSelector> selectors = new LinkedList<MetricsSelector>();

	private final List<Placeholder> placeholders;
	
	public DefaultSampler(final MetricsReader reader, final List<Placeholder> placeholders) {
		this.reader = reader;
		this.placeholders = placeholders;
	}

	public DefaultSampler addWriter(final MetricsWriter writer) {
		writers.add(writer);
		return this;
	}

	public DefaultSampler addSelector(final MetricsSelector selector) {
		selectors.add(selector);
		final Map<String, Object> transformerPlaceholders = new HashMap<String, Object>();
		transformerPlaceholders.putAll(reader.getPlaceholders());
		for (final Placeholder placeholder : placeholders) {
			transformerPlaceholders.put(placeholder.getKey(), placeholder.getValue());
		}
		selector.setPlaceholders(transformerPlaceholders);
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
		try {
			final Map<String, MetricValue> metrics = readMetrics();
			writeMetrics(metrics);
		} catch (final MetricReadException e) {
			logger.warn("Failed to read metrics", e);
		} catch (final MetricWriteException e) {
			logger.warn("Failed to write metrics", e);
		}
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
		reader.open();

		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		for (final MetricsSelector transformer : selectors) {
			final Map<String, MetricValue> metrics = transformer.readMetrics(reader);
			logger.debug("Transformer "+transformer+" returned "+metrics.size()+" metrics");
			result.putAll(metrics);
		}

		reader.close();

		return result;
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
		return getClass().getSimpleName()+"["+reader+"->"+writers+ "]";
	}
}
