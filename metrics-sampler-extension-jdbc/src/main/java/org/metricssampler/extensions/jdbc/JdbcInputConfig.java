package org.metricssampler.extensions.jdbc;

import org.metricssampler.config.InputConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

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

	/**
	 * @return the name of a jdbc-connection-pool to use to get connections
	 */
	public String getPool() {
		return pool;
	}

	/**
	 * @return the list of SQL queries to execute and interpret results as metrics
	 */
	public List<String> getQueries() {
		return queries;
	}
}
