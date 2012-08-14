package org.jmxsampler.extensions.base.transformer.regexp;

import org.jmxsampler.reader.MetricName;

public class MatchingMetric {
	private final MetricName metaData;
	private final String name;
	
	public MatchingMetric(final MetricName metaData, final String name) {
		this.metaData = metaData;
		this.name = name;
	}

	public MetricName getMetaData() {
		return metaData;
	}

	public String getName() {
		return name;
	}

	
}
