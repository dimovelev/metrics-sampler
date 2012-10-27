package org.metricssampler.extensions.jdbc;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notNegative;

import java.util.Collections;
import java.util.Map;

import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.loader.xbeans.SharedResourceXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("jdbc-connection-pool")
public class JdbcConnectionPoolXBean extends SharedResourceXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;
	
	@XStreamAsAttribute
	private String driver;

	@XStreamAsAttribute
	@XStreamAlias("min-size")
	private Integer minSize;
	
	@XStreamAsAttribute
	@XStreamAlias("max-size")
	private Integer maxSize;
	
	private JdbcOptionsXBean options;
	
	@Override
	protected void validate() {
		super.validate();
		notEmpty("username", "jdbc-connection-pool", getUsername());
		notEmpty("password", "jdbc-connection-pool", getPassword());
		notEmpty("url", "jdbc-connection-pool", getUrl());
		notEmpty("driver", "jdbc-connection-pool", getDriver());
		notNegative("min-size", "jdbc-connection-pool", getMinSize());
		greaterThanZero("max-size", "jdbc-connection-pool", getMaxSize());
		if (options != null) {
			options.validate();
		}
	}

	@Override
	protected SharedResourceConfig createConfig() {
		final Map<String, String> jdbcOptions = options != null ? options.toMap() : Collections.<String,String>emptyMap();
		final boolean ignore = getIgnored() != null ? getIgnored() : false;
		return new JdbcConnectionPoolConfig(getMinSize(), getMaxSize(), getName(), ignore, getUrl(), getDriver(), getUsername(), getPassword(), jdbcOptions);
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
}
