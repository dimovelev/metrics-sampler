package org.metricssampler.config;

/**
 * Low-level socket options
 */
public class SocketOptionsConfig {
	private final int connectTimeout;
	private final int soTimeout;
	private final boolean keepAlive;
	private final int sndBuffSize;
	private final int rcvBuffSize;

	public SocketOptionsConfig(final int connectTimeout, final int soTimeout, final boolean keepAlive, final int sndBuffSize, final int rcvBuffSize) {
		this.connectTimeout = connectTimeout;
		this.soTimeout = soTimeout;
		this.keepAlive = keepAlive;
		this.sndBuffSize = sndBuffSize;
		this.rcvBuffSize = rcvBuffSize;
	}

	public boolean hasConnectTimeout() {
		return connectTimeout > 0;
	}
	
	/**
	 * @return the connect timeout in milliseconds. {@code 0} means infinite. {@code -1} means not specified.
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}


	public boolean hasSoTimeout() {
		return soTimeout > 0;
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

	public boolean hasSndBuffSize() {
		return sndBuffSize > 0;
	}
	
	/**
	 * @return the size of the send buffer (SO_SNDBUF)
	 */
	public int getSndBuffSize() {
		return sndBuffSize;
	}

	/**
	 * @return the size of the receive buffer (SO_RCVBUF)
	 */
	public int getRcvBuffSize() {
		return rcvBuffSize;
	}

	public boolean hasRcvBuffSize() {
		return rcvBuffSize > 0;
	}

	@Override
	public String toString() {
		final StringBuilder msg = new StringBuilder(getClass().getSimpleName()).append("[");
		if (hasConnectTimeout()) {
			msg.append("connect timeout = ").append(getSoTimeout()).append(" ms ");
		}
		if (hasSoTimeout()) {
			msg.append("socket timeout = ").append(getSoTimeout()).append(" ms ");
		}
		if (hasSndBuffSize()) {
			msg.append("send buffer size = ").append(getSndBuffSize()).append(" ");
		}
		if (hasRcvBuffSize()) {
			msg.append("receive buffer size = ").append(getRcvBuffSize()).append(" ");
		}
		msg.append("keep alive = ").append(isKeepAlive()).append("]");
		return msg.toString();
	}
	
	
}
