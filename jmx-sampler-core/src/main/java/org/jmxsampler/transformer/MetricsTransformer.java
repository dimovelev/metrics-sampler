package org.jmxsampler.transformer;

import java.util.Collection;
import java.util.Map;

import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.reader.SourceMetricMetaData;

public interface MetricsTransformer {
	Map<String, MetricValue> transformMetrics(MetricsReader reader);

	void setReaderContext(Map<String, String> context);
	void setMetaData(Collection<SourceMetricMetaData> metaData);
	
	boolean hasMetrics();
}
