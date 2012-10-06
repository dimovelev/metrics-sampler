package org.metricssampler.reader;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class SimpleMetricName implements MetricName {
	private final String name;
	private final String description;
	
	public SimpleMetricName(final String name, final String description) {
		checkArgumentNotNullNorEmpty(name, "name");
		this.name = name;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + name + "]";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		final SimpleMetricName that = (SimpleMetricName) obj;
		return this.getName().equals(that.getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	
}
