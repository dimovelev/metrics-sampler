package org.metricssampler.extensions.jmx;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.metricssampler.config.InputConfig;

public class JmxInputConfig extends InputConfig {
	private final String url;
	private final String username;
	private final String password;
	private final String providerPackages;
	private final boolean persistentConnection;
	private final List<Pattern> ignoredObjectNames;
	
	public JmxInputConfig(final String name, final String url, final String username,
			final String password, final String providerPackages,
			final boolean persistentConnection, final List<Pattern> ignoredObjectNames) {
		super(name);
		this.url = url;
		this.username = username;
		this.password = password;
		this.providerPackages = providerPackages;
		this.persistentConnection = persistentConnection;
		this.ignoredObjectNames = Collections.unmodifiableList(ignoredObjectNames);
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

	public List<Pattern> getIgnoredObjectNames() {
		return ignoredObjectNames;
	}
}
