package org.metricssampler.extensions.jdbc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.Variable;

public class JdbcInputConfig extends InputConfig {
	private final String url;
	private final String driver;
	private final String username;
	private final String password;
	private final List<String> queries;
	private final Map<String, String> options;
	
	public JdbcInputConfig(final String name, final List<Variable> variables, final String url, final String driver, final String username, final String password, final List<String> queries, final Map<String, String> options) {
		super(name, variables);
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.queries = Collections.unmodifiableList(queries);
		this.options = Collections.unmodifiableMap(options);
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<String> getQueries() {
		return queries;
	}

	public Map<String, String> getOptions() {
		return options;
	}
}
