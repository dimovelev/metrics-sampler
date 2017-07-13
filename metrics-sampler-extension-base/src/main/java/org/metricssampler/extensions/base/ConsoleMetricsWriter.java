package org.metricssampler.extensions.base;

import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;
import org.metricssampler.writer.MetricWriteException;
import org.metricssampler.writer.MetricsWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * Write metrics to the standard output. This class is not thread safe and one instance should not be used by multiple samplers. Currently,
 * each sampler gets its own instance so that is not a problem.
 */
public class ConsoleMetricsWriter implements MetricsWriter {
	private final ConsoleOutputConfig config;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ConsoleMetricsWriter(final ConsoleOutputConfig config) {
		checkArgumentNotNull(config, "config");
		this.config = config;
	}

	@Override
	public void write(final Metrics metrics) {
		checkArgumentNotNull(metrics, "metrics");
		for (final Metric entry : metrics) {
			final MetricValue value = entry.getValue();
			final String timestampPrefix = dateFormat.format(new Date(value.getTimestamp())) + " ";
			System.out.println(timestampPrefix + entry.getName().getName() + "=" + value.getValue());
		}
	}

	@Override
	public void open() throws MetricWriteException {
		// Nothing to do here
	}

	@Override
	public void close() throws MetricWriteException {
		// Nothing to do here
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + config.getName() + "]";
	}
}
