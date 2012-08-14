package org.jmxsampler.reader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMetricsReader implements MetricsReader {
	private final List<MetricReaderListener> listeners = new LinkedList<MetricReaderListener>();

	@Override
	public MetricValue readMetric(final MetricName metric) throws MetricReadException {
		throw new UnsupportedOperationException("Please use readAllMetrics() because the reader always fetches them all and reading them one by one is less performant");
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		throw new UnsupportedOperationException("Please use readMetric() because the reader can only fetch one metric at a time / there are too many metrics");
	}

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
