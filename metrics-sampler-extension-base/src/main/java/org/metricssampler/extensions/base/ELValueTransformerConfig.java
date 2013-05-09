package org.metricssampler.extensions.base;

import java.util.regex.Pattern;

import org.metricssampler.config.NameRegExpValueTransformerConfig;

public class ELValueTransformerConfig extends NameRegExpValueTransformerConfig {
	private final String expression;

	public ELValueTransformerConfig(final Pattern namePattern, final String expression) {
		super(namePattern);
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}
}
