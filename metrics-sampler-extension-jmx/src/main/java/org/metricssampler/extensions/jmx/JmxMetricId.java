package org.metricssampler.extensions.jmx;

import javax.management.ObjectName;

public class JmxMetricId {
	private final ObjectName objectName;
	private final String attributeName;
	
	public JmxMetricId(final ObjectName objectName, final String attributeName) {
		this.objectName = objectName;
		this.attributeName = attributeName;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public int hashCode() {
		// TODO do something better
		return objectName.hashCode() + attributeName.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof JmxMetricId)) {
			return false;
		}
		final JmxMetricId that = (JmxMetricId) obj;
		return this.objectName.equals(that.objectName) && this.attributeName.equals(that.attributeName);
	}
	
	
	
}
