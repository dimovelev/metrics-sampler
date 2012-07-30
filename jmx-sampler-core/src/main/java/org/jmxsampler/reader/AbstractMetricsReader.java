package org.jmxsampler.reader;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractMetricsReader implements MetricsReader {
	private final List<MetricReaderListener> listeners = new LinkedList<MetricReaderListener>();

	protected void notifyOnConnected() {
		for (final MetricReaderListener listener : listeners) {
			listener.onConnected(this);
		}
	}

	@Override
	public void addListener(final MetricReaderListener listener) {
		this.listeners.add(listener);
	}
}
