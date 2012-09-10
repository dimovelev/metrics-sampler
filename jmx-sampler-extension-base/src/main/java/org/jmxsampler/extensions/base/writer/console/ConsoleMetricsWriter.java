package org.jmxsampler.extensions.base.writer.console;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.writer.MetricWriteException;
import org.jmxsampler.writer.MetricsWriter;

/**
 * Write metrics to the standard output. This class is not thread safe and should not be used by multiple samplers.
 */
public class ConsoleMetricsWriter implements MetricsWriter {
	private final ConsoleOutputConfig config;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public ConsoleMetricsWriter(final ConsoleOutputConfig config) {
		this.config = config;
	}

	@Override
	public void write(final Map<String, MetricValue> metrics) {
		for (final Map.Entry<String, MetricValue> entry : metrics.entrySet()) {
			final MetricValue value = entry.getValue();
			final String timestampPrefix = dateFormat.format(new Date(value.getTimestamp()))+" ";
			System.out.println(timestampPrefix + entry.getKey() + "=" + value.getValue());
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
