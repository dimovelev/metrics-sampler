package org.jmxsampler.extensions.graphite.writer;

import org.jmxsampler.config.WriterConfig;

public class GraphiteWriterConfig extends WriterConfig {
	private final String host;
	private final int port;
	private final String prefix;

	public GraphiteWriterConfig(final String name, final String host, final int port, final String prefix) {
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
