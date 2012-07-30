package org.jmxsampler.extensions.jmx.reader;

import org.jmxsampler.config.ReaderConfig;

public class JmxReaderConfig extends ReaderConfig {
	private final String url;
	private final String username;
	private final String password;
	private final String providerPackages;
	private final boolean persistentConnection;

	public JmxReaderConfig(final String name, final String url, final String username,
			final String password, final String providerPackages,
			final boolean persistentConnection) {
		super(name);
		this.url = url;
		this.username = username;
		this.password = password;
		this.providerPackages = providerPackages;
		this.persistentConnection = persistentConnection;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getProviderPackages() {
		return providerPackages;
	}

	public boolean isPersistentConnection() {
		return persistentConnection;
	}

}
