package org.jmxsampler.extensions.base.sampler;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricReaderListener;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.reader.SourceMetricMetaData;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.transformer.MetricsTransformer;
import org.jmxsampler.writer.MetricWriteException;
import org.jmxsampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSampler implements Sampler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MetricsReader reader;
	private final List<MetricsWriter> writers = new LinkedList<MetricsWriter>();
	private final List<MetricsTransformer> transformers = new LinkedList<MetricsTransformer>();

	public DefaultSampler(final MetricsReader reader) {
		this.reader = reader;
		reader.addListener(new MetricReaderListener() {
			@Override
			public void onConnected(final MetricsReader reader) {
				final Map<String, String> transformationContext = reader.getTransformationContext();
				final Collection<SourceMetricMetaData> metaData = reader.getMetaData();
				for (final MetricsTransformer transformer : transformers) {
					transformer.setMetaData(transformationContext, metaData);
				}
			}
		});
	}

	public DefaultSampler addWriter(final MetricsWriter writer) {
		writers.add(writer);
		return this;
	}

	public DefaultSampler addTransformer(final MetricsTransformer transformer) {
		transformers.add(transformer);
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
			final Map<String, Object> metrics = readMetrics();
			writeMetrics(metrics);
		} catch (final MetricReadException e) {
			logger.warn("Failed to read metrics", e);
		} catch (final MetricWriteException e) {
			logger.warn("Failed to write metrics", e);
		}
	}

	private void writeMetrics(final Map<String, Object> metrics) {
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

	private Map<String, Object> readMetrics() {
		reader.open();

		final Map<String, Object> result = new HashMap<String, Object>();
		for (final MetricsTransformer transformer : transformers) {
			final Map<String, Object> metrics = transformer.transformMetrics(reader);
			logger.debug("Transformer "+transformer+" returned "+metrics.size()+" metrics");
			result.putAll(metrics);
		}

		reader.close();

		return result;
	}

	public boolean check() {
		boolean result = true;
		reader.open();

		for (final MetricsTransformer transformer : transformers) {
			if (!transformer.hasMetrics()) {
				System.out.println(transformer+" has no metrics");
				result = false;
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
