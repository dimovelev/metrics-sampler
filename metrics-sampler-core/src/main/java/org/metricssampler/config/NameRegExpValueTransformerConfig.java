package org.metricssampler.config;

import java.util.regex.Pattern;

public abstract class NameRegExpValueTransformerConfig extends ValueTransformerConfig {
	private final Pattern namePattern;

	public NameRegExpValueTransformerConfig(final Pattern namePattern) {
		this.namePattern = namePattern;
	}

	public Pattern getNamePattern() {
		return namePattern;
	}
}
