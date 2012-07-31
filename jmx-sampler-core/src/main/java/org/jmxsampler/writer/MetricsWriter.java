package org.jmxsampler.writer;

import java.util.Map;

import org.jmxsampler.reader.MetricValue;

public interface MetricsWriter {
	void open() throws MetricWriteException;
	void close();

	void write(Map<String, MetricValue> metrics) throws MetricWriteException;
}
