package org.metric.sampler.extension.memcached;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.util.Map;

public class MemcachedInputConfig extends InputConfig {
	private final String host;
	private final int port;
	private final SocketOptionsConfig socketOptions;

	public MemcachedInputConfig(final String name, final Map<String, Object> variables, final String host, final int port, final SocketOptionsConfig socketOptions) {
		super(name, variables);
		this.host = host;
		this.port = port;
		this.socketOptions = socketOptions;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public SocketOptionsConfig getSocketOptions() {
		return socketOptions;
	}
}
