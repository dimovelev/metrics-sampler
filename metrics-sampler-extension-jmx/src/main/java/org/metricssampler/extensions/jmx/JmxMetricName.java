package org.metricssampler.extensions.jmx;

import org.metricssampler.reader.MetricName;

import javax.management.ObjectName;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class JmxMetricName implements MetricName {
	private final ObjectName objectName;
	private final String attributeName;
	private final PropertyPath propertyPath;
	private final String description;
	
	private final String name;
	
	public JmxMetricName(final ObjectName objectName, final String attributeName, final PropertyPath propertyPath, final String description) {
		checkArgumentNotNull(objectName, "objectName");
		checkArgumentNotNull(propertyPath, "propertyPath");
		checkArgumentNotNullNorEmpty(attributeName, "attributeName");
		this.objectName = objectName;
		this.attributeName = attributeName;
		this.propertyPath = propertyPath;
		this.description = description;
		this.name = generateName();
	}

	private String generateName() {
		final StringBuilder result = new StringBuilder(objectName.getCanonicalName());
		result.append('.').append(attributeName);
		result.append(propertyPath.toString());
		return result.toString();
	}
	
	public JmxMetricId getJmxId() {
		return new JmxMetricId(objectName, attributeName);
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

	public PropertyPath getProperty() {
		return propertyPath;
	}
	
	public boolean isComposite() {
		return propertyPath != null;
	}

	@Override
	public String toString() {
		return  getClass().getSimpleName() + "[" + name + "]";
	}
}
