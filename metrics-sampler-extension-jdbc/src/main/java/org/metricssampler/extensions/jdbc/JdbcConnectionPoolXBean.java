package org.metricssampler.extensions.jdbc;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.loader.xbeans.SharedResourceXBean;

import java.util.Collections;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.*;

@XStreamAlias("jdbc-connection-pool")
public class JdbcConnectionPoolXBean extends SharedResourceXBean {
	private static final int DEFAULT_LOGIN_TIMEOUT = 5;

	/**
	 * @see JdbcConnectionPoolConfig#getUrl()
	 */
	@XStreamAsAttribute
	private String url;

	/**
	 * @see JdbcConnectionPoolConfig#getUsername()
	 */
	@XStreamAsAttribute
	private String username;

	/**
	 * @see JdbcConnectionPoolConfig#getPassword()
	 */
	@XStreamAsAttribute
	private String password;

	/**
	 * @see JdbcConnectionPoolConfig#getDriver()
	 */
	@XStreamAsAttribute
	private String driver;

	/**
	 * @see JdbcConnectionPoolConfig#getMinSize()
	 */
	@XStreamAsAttribute
	@XStreamAlias("min-size")
	private Integer minSize;

	/**
	 * @see JdbcConnectionPoolConfig#getMaxSize()
	 */
	@XStreamAsAttribute
	@XStreamAlias("max-size")
	private Integer maxSize;

	/**
	 * @see JdbcConnectionPoolConfig#getLoginTimeout()
	 */
	@XStreamAsAttribute
	@XStreamAlias("login-timeout")
	private Integer loginTimeout;

	/**
	 * @see JdbcConnectionPoolConfig#getOptions()
	 */
	private JdbcOptionsXBean options;
	
	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "username", getUsername());
		notEmpty(this, "password", getPassword());
		notEmpty(this, "url", getUrl());
		notEmpty(this, "driver", getDriver());
		notNegative(this, "min-size", getMinSize());
		greaterThanZero(this, "max-size", getMaxSize());
		notNegativeOptional(this, "login-timeout", getLoginTimeout());
		if (options != null) {
			options.validate();
		}
	}

	@Override
	protected SharedResourceConfig createConfig() {
		final Map<String, String> jdbcOptions = options != null ? options.toMap() : Collections.<String,String>emptyMap();
		final boolean ignore = getIgnored() != null ? getIgnored() : false;
		final int loginTimeout = getLoginTimeout() != null ? getLoginTimeout() : DEFAULT_LOGIN_TIMEOUT;
		return new JdbcConnectionPoolConfig(getMinSize(), getMaxSize(), getName(), ignore, getUrl(), getDriver(), getUsername(), getPassword(), jdbcOptions, loginTimeout);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(final String driver) {
		this.driver = driver;
	}

	public JdbcOptionsXBean getOptions() {
		return options;
	}

	public void setOptions(final JdbcOptionsXBean options) {
		this.options = options;
	}

	public Integer getMinSize() {
		return minSize;
	}

	public void setMinSize(final Integer minSize) {
		this.minSize = minSize;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(final Integer maxSize) {
		this.maxSize = maxSize;
	}

	public Integer getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(final Integer loginTimeout) {
		this.loginTimeout = loginTimeout;
	}
	}
