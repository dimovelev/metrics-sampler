package org.metricssampler.extensions.base;

import org.metricssampler.config.InputConfig;

import java.util.Map;

public class SelfInputConfig extends InputConfig {
	public SelfInputConfig(final String name, final Map<String, Object> variables) {
		super(name, variables);
	}
}
