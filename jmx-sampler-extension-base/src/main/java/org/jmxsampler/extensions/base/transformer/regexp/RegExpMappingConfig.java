package org.jmxsampler.extensions.base.transformer.regexp;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.MappingConfig;

public class RegExpMappingConfig extends MappingConfig {
	private Pattern namePattern;
	private Pattern descriptionPattern;
	private final String keyExpression;


	public RegExpMappingConfig(final String namePattern, final String descriptionPattern, final String keyExpression) {
		setNamePatternStr(namePattern);
		setDescriptionPatternStr(descriptionPattern);
		this.keyExpression = keyExpression;
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
