package org.metricssampler.extensions.oranosql;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Map;

import org.metricssampler.config.InputConfig;

public class OracleNoSQLInputConfig extends InputConfig {
	private final String store;
	private final String host;
	private final int port;

	public OracleNoSQLInputConfig(final String name, final Map<String, Object> variables, final String store, final String host, final int port) {
		super(name, variables);
		checkArgumentNotNull(store, "store");
		checkArgumentNotNull(host, "host");
		this.store = store;
		this.host = host;
		this.port = port;
	}

	public String getStore() {
		return store;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
