package org.jmxsampler.transformer;

import java.util.Collection;
import java.util.Map;

import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.reader.SourceMetricMetaData;

public interface MetricsTransformer {
	Map<String, Object> transformMetrics(MetricsReader reader);

	void setMetaData(Map<String, String> readerContext, Collection<SourceMetricMetaData> metaData);
	
	boolean hasMetrics();
}
