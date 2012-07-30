package org.jmxsampler.extensions.base.writer.console;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jmxsampler.writer.MetricWriteException;
import org.jmxsampler.writer.MetricsWriter;

/**
 * Write metrics to the standard output. This class is not thread safe and should not be used by multiple samplers.
 */
public class ConsoleMetricsWriter implements MetricsWriter {
	private final ConsoleWriterConfig config;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm:hh:ss");

	public ConsoleMetricsWriter(final ConsoleWriterConfig config) {
		this.config = config;
	}

	@Override
	public void write(final Map<String, Object> metrics) {
		final String prefix = dateFormat.format(new Date())+" ";
		for (final Map.Entry<String, Object> entry : metrics.entrySet()) {
			System.out.println(prefix+entry.getKey()+"=" + entry.getValue());
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
