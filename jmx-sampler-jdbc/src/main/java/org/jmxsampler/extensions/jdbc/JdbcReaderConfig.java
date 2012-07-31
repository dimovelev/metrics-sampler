package org.jmxsampler.extensions.jdbc;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.jmxsampler.config.ReaderConfig;

public class JdbcReaderConfig extends ReaderConfig {
	private final URL url;
	private final String username;
	private final String password;
	private final List<String> queries;
	
	public JdbcReaderConfig(final String name, final URL url, final String username, final String password, final List<String> queries) {
		super(name);
		this.url = url;
		this.username = username;
		this.password = password;
		this.queries = Collections.unmodifiableList(queries);
	}

	public URL getUrl() {
		return url;
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
	
}
