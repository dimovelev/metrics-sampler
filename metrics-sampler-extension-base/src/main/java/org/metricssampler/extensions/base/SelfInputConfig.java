package org.metricssampler.extensions.base;

import java.util.Map;

import org.metricssampler.config.InputConfig;

public class SelfInputConfig extends InputConfig {
	public SelfInputConfig(final String name, final Map<String, Object> variables) {
		super(name, variables);
	}
}
