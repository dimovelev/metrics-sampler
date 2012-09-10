package org.metricssampler.selector;

import java.util.Map;

import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.MetricsReader;

public interface MetricsSelector {
	Map<String, MetricValue> readMetrics(MetricsReader reader);

	void setPlaceholders(Map<String, Object> placeholders);
	
	int getMetricCount(MetricsReader reader);
}
