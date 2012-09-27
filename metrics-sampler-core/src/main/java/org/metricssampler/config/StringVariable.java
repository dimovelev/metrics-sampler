package org.metricssampler.config;

import static com.google.common.base.Preconditions.checkNotNull;

public class StringVariable extends Variable {
	private final String value;

	public StringVariable(final String name, final String value) {
		super(name);
		checkNotNull(value, "value may not be null or empty");
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
