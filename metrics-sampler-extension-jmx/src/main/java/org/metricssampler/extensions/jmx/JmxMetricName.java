package org.metricssampler.extensions.jmx;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

import javax.management.ObjectName;

import org.metricssampler.reader.MetricName;

public class JmxMetricName implements MetricName {
	private final ObjectName objectName;
	private final String attributeName;
	private final String key;
	private final String description;
	
	private final String name;
	
	public JmxMetricName(final ObjectName objectName, final String attributeName, final String key, final String description) {
		checkArgumentNotNull(objectName, "objectName");
		checkArgumentNotNullNorEmpty(attributeName, "attributeName");
		this.objectName = objectName;
		this.attributeName = attributeName;
		this.key = key;
		this.description = description;
		this.name = generateName(objectName, attributeName, key);
	}

	private String generateName(final ObjectName objectName, final String attributeName, final String key) {
		final StringBuilder result = new StringBuilder(objectName.getCanonicalName());
		result.append('.').append(attributeName);
		if (key != null) {
			result.append('#').append(key);
		}
		return result.toString();
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
		return  getClass().getSimpleName() + "[" + name + "]";
	}
	
	
}
