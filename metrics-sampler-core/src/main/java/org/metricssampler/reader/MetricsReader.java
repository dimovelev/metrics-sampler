package org.metricssampler.reader;

import java.util.Map;

/**
 * A reader that is able to read metrics from an input.
 */
public interface MetricsReader {
	void open() throws MetricReadException;
	Iterable<MetricName> readNames();
	void close();
	Map<String, Object> getPlaceholders();
}
