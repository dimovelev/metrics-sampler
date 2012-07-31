package org.jmxsampler.reader;

public class MetricValue {
	private final long timestamp;
	private final Object value;
	
	public MetricValue(final long timestamp, final Object value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Object getValue() {
		return value;
	}
}
