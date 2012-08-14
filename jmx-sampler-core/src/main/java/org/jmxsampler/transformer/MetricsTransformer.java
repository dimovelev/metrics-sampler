package org.jmxsampler.transformer;

import java.util.Collection;
import java.util.Map;

import org.jmxsampler.reader.MetricName;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;

public interface MetricsTransformer {
	Map<String, MetricValue> transformMetrics(MetricsReader reader);

	void setReaderContext(Map<String, String> context);
	void setMetaData(Collection<MetricName> metaData);
	
	boolean hasMetrics();
	int getMetricCount();
}
