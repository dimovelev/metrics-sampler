package org.metricssampler.extensions.base.selector.regexp;

import org.metricssampler.config.SelectorConfig;

public class RegExpSelectorConfig extends SelectorConfig {
	private final String namePattern;
	private final String descriptionPattern;
	private final String keyExpression;


	public RegExpSelectorConfig(final String namePattern, final String descriptionPattern, final String keyExpression) {
		this.namePattern = namePattern;
		this.descriptionPattern = descriptionPattern;
		this.keyExpression = keyExpression;
	}

	public boolean hasNameFilter() {
		return namePattern != null;
	}

	public String getNamePattern() {
		return namePattern;
	}

	public boolean hasDescriptionFilter() {
		return descriptionPattern != null;
	}

	public String getDescriptionPattern() {
		return descriptionPattern;
	}

	public String getKeyExpression() {
		return keyExpression;
	}
}
