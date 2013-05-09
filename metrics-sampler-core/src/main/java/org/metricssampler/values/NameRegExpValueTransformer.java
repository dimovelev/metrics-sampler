package org.metricssampler.values;

import org.metricssampler.config.NameRegExpValueTransformerConfig;

public abstract class NameRegExpValueTransformer implements ValueTransformer {
	protected final NameRegExpValueTransformerConfig config;

	public NameRegExpValueTransformer(final NameRegExpValueTransformerConfig config) {
		this.config = config;
	}

	@Override
	public boolean matches(final String metric) {
		return config.getNamePattern().matcher(metric).matches();
	}
}
