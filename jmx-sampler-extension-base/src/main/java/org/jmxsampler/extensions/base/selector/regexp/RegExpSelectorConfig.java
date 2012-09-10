package org.jmxsampler.extensions.base.selector.regexp;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.SelectorConfig;

public class RegExpSelectorConfig extends SelectorConfig {
	private Pattern namePattern;
	private Pattern descriptionPattern;
	private final String keyExpression;


	public RegExpSelectorConfig(final String namePattern, final String descriptionPattern, final String keyExpression) {
		setNamePatternStr(namePattern);
		setDescriptionPatternStr(descriptionPattern);
		this.keyExpression = keyExpression;
	}

	public boolean hasNameFilter() {
		return namePattern != null;
	}

	public Pattern getNamePattern() {
		return namePattern;
	}

	protected void setNamePatternStr(final String value) {
		try {
			this.namePattern = value != null ? Pattern.compile(value) : null;
		} catch (final PatternSyntaxException e) {
			throw new ConfigurationException("Invalid name pattern \""+value+"\": "+e.getMessage(), e);
		}
	}

	public boolean hasDescriptionFilter() {
		return descriptionPattern != null;
	}

	public Pattern getDescriptionPattern() {
		return descriptionPattern;
	}

	protected void setDescriptionPatternStr(final String value) {
		try {
			this.descriptionPattern = value != null ? Pattern.compile(value) : null;
		} catch (final PatternSyntaxException e) {
			throw new ConfigurationException("Invalid description pattern \""+value+"\": "+e.getMessage(), e);
		}
	}

	public String getKeyExpression() {
		return keyExpression;
	}
}
