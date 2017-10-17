package org.metricssampler.reader;

import java.util.Objects;

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

	@Override
	public String toString() {
		return value != null ? value.toString() : "null";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MetricValue that = (MetricValue) o;
		return timestamp == that.timestamp &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestamp, value);
	}
}
