package org.metricssampler.extensions.jdbc;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.InputConfig;

public class JdbcInputConfig extends InputConfig {
	private final String pool;
	private final List<String> queries;
	
	public JdbcInputConfig(final String name, final Map<String, Object> variables, final String pool, final List<String> queries) {
		super(name, variables);
		checkArgumentNotNull(pool, "pool");
		checkArgumentNotNullNorEmpty(queries, "queries");
		this.pool= pool;
		this.queries = Collections.unmodifiableList(queries);
	}

	public String getPool() {
		return pool;
	}

	public List<String> getQueries() {
		return queries;
	}
}
