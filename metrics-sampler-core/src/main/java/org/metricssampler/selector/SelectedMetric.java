package org.metricssampler.selector;

import org.metricssampler.reader.MetricName;

public class SelectedMetric {
	private final MetricName originalName;
	private final String name;
	
	public SelectedMetric(final MetricName originalName, final String name) {
		this.originalName = originalName;
		this.name = name;
	}

	public MetricName getOriginalName() {
		return originalName;
	}

	public String getName() {
		return name;
	}
}
