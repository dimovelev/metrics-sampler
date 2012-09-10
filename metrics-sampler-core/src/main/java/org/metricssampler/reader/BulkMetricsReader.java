package org.metricssampler.reader;

import java.util.Map;

/**
 * A reader that can fetch all metrics' names and values at once. 
 */
public interface BulkMetricsReader extends MetricsReader {
	Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException;
}
