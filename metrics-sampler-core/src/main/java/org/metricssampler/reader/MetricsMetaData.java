package org.metricssampler.reader;

import java.util.Collection;
import java.util.Iterator;

public class MetricsMetaData implements Iterable<MetricName> {
	private final Collection<MetricName> names;

	public MetricsMetaData(final Collection<MetricName> names) {
		this.names = names;
	}

	@Override
	public Iterator<MetricName> iterator() {
		return names.iterator();
	}

	public int size() {
		return names.size();
	}
}
