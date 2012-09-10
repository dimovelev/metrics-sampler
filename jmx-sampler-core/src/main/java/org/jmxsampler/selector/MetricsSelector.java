package org.jmxsampler.selector;

import java.util.Map;

import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;

public interface MetricsSelector {
	Map<String, MetricValue> readMetrics(MetricsReader reader);

	void setPlaceholders(Map<String, Object> placeholders);
	
	int getMetricCount(MetricsReader reader);
}
