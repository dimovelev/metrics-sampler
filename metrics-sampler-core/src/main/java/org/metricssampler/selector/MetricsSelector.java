package org.metricssampler.selector;

import org.metricssampler.reader.Metrics;
import org.metricssampler.reader.MetricsReader;

import java.util.Map;

/**
 * Select metrics to forward to the outputs, optionally renaming them.
 */
public interface MetricsSelector {
	/**
	 * @param reader
	 * @return fetch matching metrics from the reader.
	 */
	Metrics readMetrics(MetricsReader reader);

	void setVariables(Map<String, Object> variables);
	
	/**
	 * @param reader
	 * @return the number of matching metrics
	 */
	int getMetricCount(MetricsReader reader);

	void reset();
}
