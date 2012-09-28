package org.metricssampler.extensions.modqos;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.metricssampler.config.InputConfig;

public class ModQosInputConfig extends InputConfig {
	private final URL url;
	private final String username;
	private final String password;
	private final Map<String, String> headers;
	
	public ModQosInputConfig(final String name, final  Map<String, Object> variables, final URL url, final String username, final String password, final Map<String, String> headers) {
		super(name, variables);
		checkArgumentNotNull(url, "url");
		checkArgumentNotNull(headers, "headers");
		this.url = url;
		this.username = username;
		this.password = password;
		this.headers = Collections.unmodifiableMap(headers);
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
}
