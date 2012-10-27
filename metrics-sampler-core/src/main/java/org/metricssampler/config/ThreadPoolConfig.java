package org.metricssampler.config;

public class ThreadPoolConfig extends SharedResourceConfig {
	private final int coreSize;
	private final int maxSize;
	private final int keepAliveTime;


	public ThreadPoolConfig(final String name, final boolean ignored, final int coreSize, final int maxSize, final int keepAliveTime) {
		super(name, ignored);
		this.coreSize = coreSize;
		this.maxSize = maxSize;
		this.keepAliveTime = keepAliveTime;
	}

	public int getCoreSize() {
		return coreSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}
}
