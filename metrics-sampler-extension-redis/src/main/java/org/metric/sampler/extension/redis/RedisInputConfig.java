package org.metric.sampler.extension.redis;

import java.util.Map;

import org.metricssampler.config.InputConfig;

public class RedisInputConfig extends InputConfig {
	private final String host;
	private final int port;
	private final String password;
	
	public RedisInputConfig(final String name, final Map<String, Object> variables, final String host, final int port, final String password) {
		super(name, variables);
		this.host = host;
		this.port = port;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public boolean hasPassword() {
		return password != null && !password.equals("");
	}
}
