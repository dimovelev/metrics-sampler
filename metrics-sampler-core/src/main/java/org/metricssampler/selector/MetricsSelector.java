package org.metricssampler.selector;

import java.util.Map;

import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.MetricsReader;

/**
 * Select metrics to forward to the outputs, optionally renaming them.
 */
public interface MetricsSelector {
	/**
	 * @param reader
	 * @return fetch matching metrics from the reader.
	 */
	Map<String, MetricValue> readMetrics(MetricsReader reader);

	void setVariables(Map<String, Object> variables);
	
	/**
	 * @param reader
	 * @return the number of matching metrics
	 */
	int getMetricCount(MetricsReader reader);
}
