package org.jmxsampler.extensions.base.transformer.regexp;

import org.jmxsampler.reader.SourceMetricMetaData;

public class MatchingMetric {
	private final SourceMetricMetaData metaData;
	private final String name;
	
	public MatchingMetric(SourceMetricMetaData metaData, String name) {
		this.metaData = metaData;
		this.name = name;
	}

	public SourceMetricMetaData getMetaData() {
		return metaData;
	}

	public String getName() {
		return name;
	}

	
}
