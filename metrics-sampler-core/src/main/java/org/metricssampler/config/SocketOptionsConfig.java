package org.metricssampler.config;

public class SocketOptionsConfig {
	private final int soTimeout;
	private final boolean keepAlive;

	public SocketOptionsConfig(final int soTimeout, final boolean keepAlive) {
		this.soTimeout = soTimeout;
		this.keepAlive = keepAlive;
	}

	/**
	 * @return the SO_TIMEOUT in milliseconds
	 */
	public int getSoTimeout() {
		return soTimeout;
	}
	
	/**
	 * @return {@code true} if TCP_KEEP_ALIVE should be activated
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}
}
