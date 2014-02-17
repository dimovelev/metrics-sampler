package org.metricssampler.extensions.http;

import java.net.URL;
import java.util.Map;

import org.metricssampler.config.BaseHttpInputConfig;

public class HttpInputConfig extends BaseHttpInputConfig {
	private final HttpResponseParser parser;

	protected HttpInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username, final String password,
			final Map<String, String> headers, final boolean preemptiveAuthEnabled, final HttpResponseParser parser) {
		super(name, variables, url, username, password, headers, preemptiveAuthEnabled);
		this.parser = parser;
	}

	public HttpResponseParser getParser() {
		return parser;
	}
}
