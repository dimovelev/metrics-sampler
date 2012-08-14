package org.jmxsampler.transformer;

import java.util.Map;

import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;

public interface MetricsTransformer {
	Map<String, MetricValue> transformMetrics(MetricsReader reader);

	void setReaderContext(Map<String, String> context);
	
	int getMetricCount(MetricsReader reader);
}
