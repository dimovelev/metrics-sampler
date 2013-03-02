package org.metricssampler.extensions.webmethods;

import java.net.URL;
import java.text.DateFormat;
import java.util.Map;

import org.metricssampler.config.HttpInputConfig;

public class WebMethodsInputConfig extends HttpInputConfig {
	private final long maxEntrySize;
	private final DateFormat dateFormat;

	public WebMethodsInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username, final String password,
			final Map<String, String> headers, final boolean preemptiveAuthEnabled, final long maxEntrySize, final DateFormat dateFormat) {
		super(name, variables, url, username, password, headers, preemptiveAuthEnabled);
		this.maxEntrySize = maxEntrySize;
		this.dateFormat = dateFormat;
	}

	public long getMaxEntrySize() {
		return maxEntrySize;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}
}
