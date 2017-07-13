package org.metricssampler.extensions.jmx;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JmxInputConfig extends InputConfig {
	private final String url;
	private final String username;
	private final String password;
	private final String providerPackages;
	private final boolean persistentConnection;
	private final List<Pattern> ignoredObjectNames;
	private final Map<String, String> connectionProperties;
	private final SocketOptionsConfig socketOptions;

	public JmxInputConfig(final String name, final  Map<String, Object> variables, final String url, final String username,
			final String password, final String providerPackages,
			final boolean persistentConnection, final List<Pattern> ignoredObjectNames, final Map<String, String> connectionProperties, final SocketOptionsConfig socketOptions) {
		super(name, variables);
		this.url = url;
		this.username = username;
		this.password = password;
		this.providerPackages = providerPackages;
		this.persistentConnection = persistentConnection;
		this.ignoredObjectNames = Collections.unmodifiableList(ignoredObjectNames);
		this.connectionProperties = Collections.unmodifiableMap(connectionProperties);
		this.socketOptions = socketOptions;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getProviderPackages() {
		return providerPackages;
	}

	public boolean isPersistentConnection() {
		return persistentConnection;
	}

	public List<Pattern> getIgnoredObjectNames() {
		return ignoredObjectNames;
	}

	/**
	 * @return The additional connection properties used when setting up the environment for the JMX connector factory.
	 */
	public Map<String, String> getConnectionProperties() {
		return connectionProperties;
	}

	public boolean hasSocketOptions() {
		return socketOptions != null;
	}

	/**
	 * @return the socket options for the low level sockets. If set, the reader will override the socket factory with one that will set those options.
	 */
	public SocketOptionsConfig getSocketOptions() {
		return socketOptions;
	}
}
