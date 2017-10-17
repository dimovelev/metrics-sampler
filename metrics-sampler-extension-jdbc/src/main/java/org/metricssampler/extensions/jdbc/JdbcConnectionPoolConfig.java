package org.metricssampler.extensions.jdbc;

import org.metricssampler.config.SharedResourceConfig;

import java.util.Collections;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class JdbcConnectionPoolConfig extends SharedResourceConfig {
	private final int minSize;
	private final int maxSize;
	private final String url;
	private final String driver;
	private final String username;
	private final String password;
	private final Map<String, String> options;
	private final int loginTimeout;
	
	public JdbcConnectionPoolConfig(final int minSize, final int maxSize, final String name, final boolean ignored, final String url, final String driver, final String username, final String password, final Map<String, String> options, final int loginTimeout) {
		super(name, ignored);
		checkArgumentNotNullNorEmpty(url, "url");
		checkArgument(maxSize >= minSize, "Max size must be greater than min size");
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.options = Collections.unmodifiableMap(options);
		this.loginTimeout = loginTimeout;
	}

	/**
	 * @return the minimal number of jdbc connections to keep in the pool
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * @return the maximal number of jdbc connections to keep in the pool.
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the URL to connect to the database
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the fully qualified class-name of the JDBC driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @return the user to connect as
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the user's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return a map of additional, low-level options to pass to the driver when creating connections.
	 */
	public Map<String, String> getOptions() {
		return options;
	}

	/**
	 * @return the login timeout in seconds
	 */
	public int getLoginTimeout() {
		return loginTimeout;
	}
}
