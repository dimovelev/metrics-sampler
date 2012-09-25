package org.metricssampler.config;

public class StringVariable extends Variable {
	private final String value;

	public StringVariable(final String name, final String value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
}
