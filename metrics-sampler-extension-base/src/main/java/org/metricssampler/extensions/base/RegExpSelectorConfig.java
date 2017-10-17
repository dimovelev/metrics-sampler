package org.metricssampler.extensions.base;

import org.metricssampler.config.SelectorConfig;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

public class RegExpSelectorConfig extends SelectorConfig {
	private final String namePattern;
	private final String descriptionPattern;
	private final String keyExpression;


	public RegExpSelectorConfig(final String namePattern, final String descriptionPattern, final String keyExpression) {
		checkArgumentNotNull(keyExpression, "keyExpression");
		this.namePattern = namePattern;
		this.descriptionPattern = descriptionPattern;
		this.keyExpression = keyExpression;
	}

	public String getNamePattern() {
		return namePattern;
	}

	public String getDescriptionPattern() {
		return descriptionPattern;
	}

	public String getKeyExpression() {
		return keyExpression;
	}
}
