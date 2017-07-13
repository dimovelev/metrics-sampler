package org.metricssampler.extensions.http;

import org.metricssampler.config.BaseHttpInputConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.net.URL;
import java.util.Map;

public class HttpInputConfig extends BaseHttpInputConfig {
	private final HttpResponseParser parser;

	protected HttpInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username, final String password,
			final Map<String, String> headers, final boolean preemptiveAuthEnabled, final SocketOptionsConfig socketOptions, final HttpResponseParser parser) {
		super(name, variables, url, username, password, headers, preemptiveAuthEnabled, socketOptions, null);
		this.parser = parser;
	}

	public HttpResponseParser getParser() {
		return parser;
	}
}
