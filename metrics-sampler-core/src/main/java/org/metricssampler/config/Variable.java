package org.metricssampler.config;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
/**
 * Base class for variables.
 */
public abstract class Variable {
	private final String name;

	public Variable(final String name) {
		checkArgument(!isNullOrEmpty(name), "name may not be null or empty");
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract Object getValue();
}
