package org.metricssampler.config;

public class ThreadPoolConfig extends SharedResourceConfig {
	private final int size;

	public ThreadPoolConfig(final String name, final boolean ignored, final int size) {
		super(name, ignored);
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
