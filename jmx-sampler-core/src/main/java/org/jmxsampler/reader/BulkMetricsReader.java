package org.jmxsampler.reader;

import java.util.Map;

public interface BulkMetricsReader extends MetricsReader {
	Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException;
}
