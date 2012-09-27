package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class StringVariable extends Variable {
	private final String value;

	public StringVariable(final String name, final String value) {
		super(name);
		checkArgumentNotNullNorEmpty(value, "value");
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
