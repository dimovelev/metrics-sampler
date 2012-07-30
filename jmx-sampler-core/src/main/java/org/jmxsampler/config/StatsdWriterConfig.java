package org.jmxsampler.config;

public class StatsdWriterConfig extends WriterConfig {
	private final String host;
	private final int port;

	public StatsdWriterConfig(final String name, final String host, final int port) {
		super(name);
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
