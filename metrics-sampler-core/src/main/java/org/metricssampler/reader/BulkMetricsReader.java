package org.metricssampler.reader;

/**
 * A reader that can fetch all metrics' names and values at once. 
 */
public interface BulkMetricsReader extends MetricsReader {
	Metrics readAllMetrics() throws MetricReadException;

	default Iterable<MetricName> readNames() {
		return readAllMetrics().getNames();
	}

}
