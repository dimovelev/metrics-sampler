package org.metricssampler.extensions.jdbc;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("jdbc")
public class JdbcInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;
	
	@XStreamAsAttribute
	private String driver;

	private JdbcOptionsXBean options;
	
	@XStreamImplicit(itemFieldName="query")
	private List<String> queries;
	
	@Override
	protected void validate() {
		super.validate();
		notEmpty("username", "jdbc-reader", getUsername());
		notEmpty("password", "jdbc-reader", getPassword());
		notEmpty("url", "jdbc-reader", getUrl());
		notEmpty("driver", "jdbc-reader", getDriver());
		notEmpty("queries", "jdbc-reader", getQueries());
		if (options != null) {
			options.validate();
		}
	}

	@Override
	protected InputConfig createConfig() {
		final Map<String, String> jdbcOptions = options != null ? options.toMap() : Collections.<String,String>emptyMap();
		return new JdbcInputConfig(getName(), getVariablesConfig(), getUrl(), getDriver(), getUsername(), getPassword(), getQueries(), jdbcOptions);
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

	public List<String> getQueries() {
		return queries;
	}

	public void setQueries(final List<String> queries) {
		this.queries = queries;
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
}
