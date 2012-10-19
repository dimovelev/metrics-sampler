package org.metricssampler.extensions.oranosql;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.Map;

import org.metricssampler.config.InputConfig;

public class OracleNoSQLInputConfig extends InputConfig {
	private final String storeName;
	private final String[] hosts;
	
	public OracleNoSQLInputConfig(final String name, final Map<String, Object> variables, final String storeName, final String[] hosts) {
		super(name, variables);
		checkArgumentNotNull(storeName, "storeName");
		checkArgumentNotNull(hosts, "hosts");
		this.storeName = storeName;
		this.hosts = hosts;
	}

	public String getStoreName() {
		return storeName;
	}

	public String[] getHosts() {
		return hosts;
	}
}
