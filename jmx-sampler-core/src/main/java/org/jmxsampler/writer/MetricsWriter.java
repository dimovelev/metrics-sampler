package org.jmxsampler.writer;

import java.util.Map;

public interface MetricsWriter {
	void open() throws MetricWriteException;
	void close();

	void write(Map<String, Object> metrics) throws MetricWriteException;
}
