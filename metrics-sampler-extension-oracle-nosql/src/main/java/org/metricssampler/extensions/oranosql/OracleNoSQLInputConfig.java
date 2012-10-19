package org.metricssampler.extensions.oranosql;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Map;

import org.metricssampler.config.InputConfig;

public class OracleNoSQLInputConfig extends InputConfig {
	private final String storeName;
	private final String host;
	private final int port;

	public OracleNoSQLInputConfig(final String name, final Map<String, Object> variables, final String storeName, final String host, final int port) {
		super(name, variables);
		checkArgumentNotNull(storeName, "storeName");
		checkArgumentNotNull(host, "host");
		this.storeName = storeName;
		this.host = host;
		this.port = port;
	}

	public String getStoreName() {
		return storeName;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
