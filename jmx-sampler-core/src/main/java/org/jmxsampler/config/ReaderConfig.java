package org.jmxsampler.config;

public abstract class ReaderConfig {
	private final String name;

	public ReaderConfig(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
