package org.metricssampler.writer;

import java.util.Map;

import org.metricssampler.reader.MetricValue;

public interface MetricsWriter {
	void open() throws MetricWriteException;
	void close();

	void write(Map<String, MetricValue> metrics) throws MetricWriteException;
}
