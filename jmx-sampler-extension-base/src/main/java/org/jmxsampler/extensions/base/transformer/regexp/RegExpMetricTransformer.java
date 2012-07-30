package org.jmxsampler.extensions.base.transformer.regexp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.reader.SourceMetricMetaData;
import org.jmxsampler.transformer.MetricsTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExpMetricTransformer implements MetricsTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RegExpMappingConfig config;
	private List<MatchingMetric> matchingMetrics;

	public RegExpMetricTransformer(final RegExpMappingConfig config) {
		this.config = config;
	}

	@Override
	public Map<String, Object> transformMetrics(final MetricsReader reader) {
		assertMatchingMetrics();
		final Map<String, Object> result = new HashMap<String, Object>();
		for (final MatchingMetric bean : matchingMetrics) {
			try {
				final Object value = reader.readMetric(bean.getMetaData());
				result.put(bean.getName(), value);
			} catch (final MetricReadException e) {
				logger.warn("Failed to read "+bean.getMetaData(), e);
			}
		}
		return result;
	}

	protected void assertMatchingMetrics() {
		if (matchingMetrics == null) {
			throw new IllegalStateException("setMetaData not called");
		}
	}

	@Override
	public void setMetaData(final Map<String, String> readerContext, final Collection<SourceMetricMetaData> metaData) {
		final Pattern namePattern = config.getNamePattern();
		final Pattern descriptionPattern = config.getDescriptionPattern();
		matchingMetrics = new LinkedList<MatchingMetric>();
		for (final SourceMetricMetaData metricMetaData : metaData) {
			Map<String, String> context = null;

			if (namePattern != null) {
				final Matcher nameMatcher = namePattern.matcher(metricMetaData.getName());
				if (!nameMatcher.matches()) {
					continue;
				}
				context = addGroups(nameMatcher, "name", context);
			}
			if (descriptionPattern != null) {
				final Matcher descriptionMatcher = descriptionPattern.matcher(metricMetaData.getDescription());
				if (!descriptionMatcher.matches()) {
					continue;
				}
				context = addGroups(descriptionMatcher, "description", context);
			}
			context.putAll(readerContext);
			String key = config.getKeyExpression();
			for (final Map.Entry<String, String> entry : context.entrySet()) {
				key = key.replace("${"+entry.getKey()+"}", entry.getValue());
			}
			final MatchingMetric matchingMetric = new MatchingMetric(metricMetaData, key);
			matchingMetrics.add(matchingMetric);
		}
		if (matchingMetrics.isEmpty()) {
			logger.warn(this+" matched no metrics");
		}
	}

	private Map<String, String> addGroups(final Matcher matcher, final String prefix, final Map<String, String> context) {
		final Map<String, String> result = context == null ? new HashMap<String, String>() : context;
		for (int i=0; i<=matcher.groupCount(); i++) {
			result.put(prefix+"["+i+"]", matcher.group(i));
		}
		return result;
	}

	@Override
	public boolean hasMetrics() {
		assertMatchingMetrics();
		return !matchingMetrics.isEmpty();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"[name-regexp="+config.getNamePattern()+",description-regexp="+config.getDescriptionPattern()+"]";
	}
}
