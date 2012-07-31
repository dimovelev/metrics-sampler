package org.jmxsampler.extensions.base.transformer.regexp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.reader.SourceMetricMetaData;
import org.jmxsampler.transformer.MetricsTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExpMetricTransformer implements MetricsTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RegExpMappingConfig config;
	private List<MatchingMetric> matchingMetrics;
	private Map<String, String> readerContext;
	
	public RegExpMetricTransformer(final RegExpMappingConfig config) {
		this.config = config;
	}

	@Override
	public Map<String, MetricValue> transformMetrics(final MetricsReader reader) {
		if (matchingMetrics == null) {
			return transformAll(reader);
		} else {
			return transformMatching(reader);
		}
	}
	
	protected Map<String, MetricValue> transformAll(final MetricsReader reader) {
		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		final Map<SourceMetricMetaData, MetricValue> metrics = reader.readAllMetrics();
		for (final Map.Entry<SourceMetricMetaData, MetricValue> entry : metrics.entrySet()) {
			final MatchingMetric metric = match(entry.getKey());
			if (metric != null) {
				result.put(metric.getName(), entry.getValue());
			}
		}
		return result;
	}
	
	protected Map<String, MetricValue> transformMatching(final MetricsReader reader) {
		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		for (final MatchingMetric bean : matchingMetrics) {
			try {
				final MetricValue value = reader.readMetric(bean.getMetaData());
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
	public void setReaderContext(final Map<String, String> readerContext) {
		this.readerContext = readerContext;
	}

	@Override
	public void setMetaData(final Collection<SourceMetricMetaData> metaData) {
		matchingMetrics = new LinkedList<MatchingMetric>();
		for (final SourceMetricMetaData metricMetaData : metaData) {
			final MatchingMetric matchingMetric = match(metricMetaData);
			if (matchingMetric != null) {
				matchingMetrics.add(matchingMetric);
			}
		}
		if (matchingMetrics.isEmpty()) {
			logger.warn(this+" matched no metrics");
		}
	}

	protected MatchingMetric match(final SourceMetricMetaData from) {
		Map<String, String> context = null;

		if (config.hasNameFilter()) {
			final Matcher nameMatcher = config.getNamePattern().matcher(from.getName());
			if (!nameMatcher.matches()) {
				return null;
			}
			context = addGroups(nameMatcher, "name", context);
		}
		if (config.hasDescriptionFilter()) {
			final Matcher descriptionMatcher = config.getDescriptionPattern().matcher(from.getDescription());
			if (!descriptionMatcher.matches()) {
				return null;
			}
			context = addGroups(descriptionMatcher, "description", context);
		}
		context.putAll(readerContext);
		String name = config.getKeyExpression();
		for (final Map.Entry<String, String> entry : context.entrySet()) {
			name = name.replace("${"+entry.getKey()+"}", entry.getValue());
		}
		return new MatchingMetric(from, name);
		
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
