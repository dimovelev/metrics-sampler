package org.metricssampler.extensions.graphite;

import org.metricssampler.config.OutputConfig;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class GraphiteOutputConfig extends OutputConfig {
	private final String host;
	private final int port;
	private final String prefix;

	public GraphiteOutputConfig(final String name, final boolean default_, final String host, final int port, final String prefix) {
		super(name, default_);
		checkArgumentNotNullNorEmpty(host, "host");
		checkArgument(port > 0 && port < 65536, "port must be in range [1,65535]");
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
