package org.metricssampler.config;

public abstract class SharedResourceConfig extends NamedConfig {
	private final boolean ignored;

	public SharedResourceConfig(final String name, final boolean ignored) {
		super(name);
		this.ignored = ignored;
	}

	public boolean isIgnored() {
		return ignored;
	}
}
