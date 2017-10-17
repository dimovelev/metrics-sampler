package org.metric.sampler.extension.redis;

import org.metricssampler.config.InputConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedisInputConfig extends InputConfig {
	private final String host;
	private final int port;
	private final String password;
	private final List<RedisCommand> commands;

	public RedisInputConfig(final String name, final Map<String, Object> variables, final String host, final int port, final String password, final List<RedisCommand> commands) {
		super(name, variables);
		this.host = host;
		this.port = port;
		this.password = password;
		this.commands = Collections.unmodifiableList(commands);
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

	public List<RedisCommand> getCommands() {
		return commands;
	}
}
