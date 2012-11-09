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

	/**
	 * @return the number of threads to always keep in the thread pool even if they are idle
	 * @see java.util.concurrent.ThreadPoolExecutor#getCorePoolSize()
	 */
	public int getCoreSize() {
		return coreSize;
	}

	/**
	 * @return the maximum number of threads in the thread pool
	 * @see java.util.concurrent.ThreadPoolExecutor#getMaximumPoolSize()
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the number of seconds before an idle thread is disposed (if the number of threads is greater then the core pool size).
	 * @see java.util.concurrent.ThreadPoolExecutor#getKeepAliveTime(java.util.concurrent.TimeUnit)
	 */
	public int getKeepAliveTime() {
		return keepAliveTime;
	}
}
