package org.metricssampler.extensions.base;

import org.metricssampler.reader.MetricName;
import org.metricssampler.selector.AbstractMetricsSelector;
import org.metricssampler.selector.SelectedMetric;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * Select metrics using regular expressions and rename them using an expression that can contain variables.
 */
public class RegExpMetricsSelector extends AbstractMetricsSelector {
	private final RegExpSelectorConfig config;

	private Pattern namePattern;
	private Pattern descriptionPattern;

	public RegExpMetricsSelector(final RegExpSelectorConfig config) {
		checkArgumentNotNull(config, "config");
		this.config = config;
	}

	@Override
	protected void doAfterVariablesSet(final Map<String, Object> variables) {
		this.namePattern = createPattern(config.getNamePattern());
		this.descriptionPattern = createPattern(config.getDescriptionPattern());
	}

	protected Pattern createPattern(final String text) {
		if (text != null && !"".equals(text)) {
			final String pattern = replaceVariables(text);
			return Pattern.compile(pattern);
		} else {
			return null;
		}
	}

	@Override
	protected SelectedMetric selectMetric(final MetricName from) {
		Map<String, Object> context = null;

		if (namePattern != null) {
			final Matcher nameMatcher = namePattern.matcher(from.getName());
			if (!nameMatcher.matches()) {
				return null;
			}
			context = addGroups(nameMatcher, "name", context);
		}
		if (descriptionPattern != null) {
			final Matcher descriptionMatcher = descriptionPattern.matcher(from.getDescription());
			if (!descriptionMatcher.matches()) {
				return null;
			}
			context = addGroups(descriptionMatcher, "description", context);
		}
		addVariables(context);
		final String newName = replaceVariables(config.getKeyExpression(), context);
		return new SelectedMetric(from, newName);

	}

	private Map<String, Object> addGroups(final Matcher matcher, final String prefix, final Map<String, Object> context) {
		final Map<String, Object> result = context == null ? new HashMap<String, Object>() : context;
		for (int i=0; i<=matcher.groupCount(); i++) {
			result.put(prefix+"["+i+"]", matcher.group(i));
		}
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append(getClass().getSimpleName()).append('[');
		boolean needsAnd = false;
		if (namePattern != null) {
			result.append("name =~ /").append(namePattern).append('/');
			needsAnd = true;
		}
		if (descriptionPattern != null) {
			if (needsAnd) {
				result.append(" and ");
			}
			result.append("description =~ /").append(descriptionPattern).append('/');
			needsAnd = true;
		}
		result.append(']');
		return result.toString();
	}
}
