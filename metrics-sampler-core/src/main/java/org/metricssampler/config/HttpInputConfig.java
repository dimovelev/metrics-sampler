package org.metricssampler.config;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.net.URL;
import java.util.Collections;
import java.util.Map;


public abstract class HttpInputConfig extends InputConfig {

	protected final URL url;
	protected final String username;
	protected final String password;
	protected final Map<String, String> headers;
	protected final boolean preemptiveAuthEnabled;

	protected HttpInputConfig(final String name, final  Map<String, Object> variables, final URL url, final String username, final String password, final Map<String, String> headers, final boolean preemptiveAuthEnabled) {
		super(name, variables);
		checkArgumentNotNull(url, "url");
		checkArgumentNotNull(headers, "headers");
		this.url = url;
		this.username = username;
		this.password = password;
		this.headers = Collections.unmodifiableMap(headers);
		this.preemptiveAuthEnabled = preemptiveAuthEnabled;
	}

	public URL getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public boolean isPreemtiveAuthEnabled() {
		return preemptiveAuthEnabled;
	}

}