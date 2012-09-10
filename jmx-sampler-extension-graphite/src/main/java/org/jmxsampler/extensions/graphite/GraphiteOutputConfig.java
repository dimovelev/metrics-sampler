package org.jmxsampler.extensions.graphite;

import org.jmxsampler.config.OutputConfig;

public class GraphiteOutputConfig extends OutputConfig {
	private final String host;
	private final int port;
	private final String prefix;

	public GraphiteOutputConfig(final String name, final String host, final int port, final String prefix) {
		super(name);
		this.host = host;
		this.port = port;
		this.prefix = prefix;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPrefix() {
		return prefix;
	}
}
