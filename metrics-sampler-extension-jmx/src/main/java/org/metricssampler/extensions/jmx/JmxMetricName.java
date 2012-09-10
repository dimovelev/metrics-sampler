package org.metricssampler.extensions.jmx;

import javax.management.ObjectName;

import org.metricssampler.reader.MetricName;

public class JmxMetricName implements MetricName {
	private final ObjectName objectName;
	private final String attributeName;
	private final String key;
	private final String description;
	
	private final String name;
	
	public JmxMetricName(final ObjectName objectName, final String attributeName, final String key, final String description) {
		this.objectName = objectName;
		this.attributeName = attributeName;
		this.key = key;
		this.description = description;
		final StringBuilder name = new StringBuilder();
		name.append(objectName.getCanonicalName()).append('.').append(attributeName);
		if (key != null) {
			name.append('#').append(key);
		}
		
		this.name = name.toString();
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getDescription() {
		return description;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getKey() {
		return key;
	}
	
	public boolean isComposite() {
		return key != null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + objectName.getCanonicalName() + "#" + attributeName + "]";
	}
	
	
}
