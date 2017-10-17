package org.metricssampler.extensions.apachestatus;

import org.metricssampler.config.BaseHttpInputConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.net.URL;
import java.util.Map;

public class ApacheStatusInputConfig extends BaseHttpInputConfig {

	protected ApacheStatusInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username, final String password,
			final Map<String, String> headers, final boolean preemptiveAuthEnabled, final SocketOptionsConfig socketOptions) {
		super(name, variables, url, username, password, headers, preemptiveAuthEnabled, socketOptions, null);
	}
}
