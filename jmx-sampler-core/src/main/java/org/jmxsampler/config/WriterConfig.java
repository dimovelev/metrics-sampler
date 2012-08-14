package org.jmxsampler.config;

public abstract class WriterConfig {
	private final String name;

	public WriterConfig(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}
}
