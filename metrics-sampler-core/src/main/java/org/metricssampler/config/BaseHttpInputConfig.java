package org.metricssampler.config;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

public abstract class BaseHttpInputConfig extends InputConfig {
	private final URL url;
	private final String username;
	private final String password;
	private final Map<String, String> headers;
	private final boolean preemptiveAuthEnabled;
	private final SocketOptionsConfig socketOptions;
	private final HttpConnectionPoolConfig connectionPool;

	protected BaseHttpInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username,
								  final String password, final Map<String, String> headers, final boolean preemptiveAuthEnabled, final SocketOptionsConfig socketOptions, HttpConnectionPoolConfig connectionPool) {
		super(name, variables);
		checkArgumentNotNull(url, "url");
		checkArgumentNotNull(headers, "headers");
		this.url = url;
		this.username = username;
		this.password = password;
		this.headers = Collections.unmodifiableMap(headers);
		this.preemptiveAuthEnabled = preemptiveAuthEnabled;
		this.socketOptions = socketOptions;
		this.connectionPool = connectionPool;
	}

	/**
	 * @return The HTTP URL that will be queried and its response processed by the input.
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @return The username to use when fetching the URL over HTTP. {@code null} if no authentication should be performed.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The password to use when fetching the URL over HTTP. May be {@code null}.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return A map of HTTP headers to use when fetching the URL over HTTP. May be an empty map but can not be {@code null}.
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @return {@code true} if the credentials should be sent directly with the first HTTP request. This might be considered a minor
	 *         security problem as we will send the credentials without any server validation. The advantage of preemptive authentication is
	 *         that we require just one HTTP request instead of two (for basic authentication). {@code true} by default.
	 */
	public boolean isPreemptiveAuthEnabled() {
		return preemptiveAuthEnabled;
	}

	/**
	 * @return The socket options to use when creating sockets. {@code null} to use the defaults.
	 */
	public SocketOptionsConfig getSocketOptions() {
		return socketOptions;
	}

	/**
	 * @return the connection pool configuration. If null, no connection pooling will be configured.
	 */
	public HttpConnectionPoolConfig getConnectionPool() {
		return connectionPool;
	}
}