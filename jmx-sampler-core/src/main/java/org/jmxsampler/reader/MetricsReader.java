package org.jmxsampler.reader;

import java.util.Map;

public interface MetricsReader {
	void open() throws MetricReadException;
	Iterable<MetricName> readNames();
	void close();
	Map<String, String> getTransformationContext();
}
