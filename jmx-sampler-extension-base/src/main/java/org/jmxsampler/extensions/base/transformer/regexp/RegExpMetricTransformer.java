package org.jmxsampler.extensions.base.transformer.regexp;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.jmxsampler.reader.BulkMetricsReader;
import org.jmxsampler.reader.MetaDataMetricsReader;
import org.jmxsampler.reader.MetricName;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsMetaData;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.transformer.MatchingMetric;
import org.jmxsampler.transformer.MetricsTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExpMetricTransformer implements MetricsTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RegExpMappingConfig config;
	private Map<String, String> readerContext;

	private MetricsMetaData cachedMetaData;
	private List<MatchingMetric> cachedMatchingMetrics;
	
	public RegExpMetricTransformer(final RegExpMappingConfig config) {
		this.config = config;
	}

	@Override
	public Map<String, MetricValue> transformMetrics(final MetricsReader reader) {
		if (reader instanceof MetaDataMetricsReader) {
			return transformMatching((MetaDataMetricsReader) reader);
		} else if (reader instanceof BulkMetricsReader) {
			return transformAll((BulkMetricsReader) reader);
		} else {
			throw new IllegalArgumentException("Unsupported reader type: " + reader);
		}
	}
	
	protected Map<String, MetricValue> transformAll(final BulkMetricsReader reader) {
		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		final Map<MetricName, MetricValue> metrics = reader.readAllMetrics();
		for (final Map.Entry<MetricName, MetricValue> entry : metrics.entrySet()) {
			final MatchingMetric metric = match(entry.getKey());
			if (metric != null) {
				result.put(metric.getName(), entry.getValue());
			}
		}
		return result;
	}
	
	protected Map<String, MetricValue> transformMatching(final MetaDataMetricsReader reader) {
		final List<MatchingMetric> matchingMetrics = getMatchingMetrics(reader);
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
		if (cachedMatchingMetrics == null) {
			throw new IllegalStateException("setMetaData not called");
		}
	}

	
	@Override
	public void setReaderContext(final Map<String, String> readerContext) {
		this.readerContext = readerContext;
	}

	private List<MatchingMetric> getMatchingMetrics(final MetaDataMetricsReader reader) {
		final MetricsMetaData metaData = reader.getMetaData();
		if (this.cachedMetaData != metaData) {
			this.cachedMatchingMetrics = matchMetrics(metaData);
			if (cachedMatchingMetrics.isEmpty()) {
				logger.warn(this+" matched no metrics");
			}
		}
		return cachedMatchingMetrics;
	}

	private List<MatchingMetric> matchMetrics(final Iterable<MetricName> names) {
		final List<MatchingMetric> result = new LinkedList<MatchingMetric>();
		for (final MetricName name : names) {
			final MatchingMetric matchingMetric = match(name);
			if (matchingMetric != null) {
				result.add(matchingMetric);
			}
		}
		return Collections.unmodifiableList(result);
	}

	protected MatchingMetric match(final MetricName from) {
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
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append(getClass().getSimpleName()).append('[');
		boolean needsAnd = false;
		if (config.hasNameFilter()) {
			result.append("name =~ /").append(config.getNamePattern()).append('/');
			needsAnd = true;
		}
		if (config.hasDescriptionFilter()) {
			if (needsAnd) {
				result.append(" and ");
			}
			result.append("description =~ /").append(config.getDescriptionPattern()).append('/');
			needsAnd = true;
		}
		result.append(']');
		return result.toString();
	}

	@Override
	public int getMetricCount(final MetricsReader reader) {
		Iterable<MetricName> names;
		if (reader instanceof MetaDataMetricsReader) {
			names = ((MetaDataMetricsReader) reader).getMetaData();
		} else if (reader instanceof BulkMetricsReader) {
			names = ((BulkMetricsReader) reader).readAllMetrics().keySet();
		} else {
			throw new IllegalArgumentException("Unsupported metrics reader: " + reader);
		}
		final List<MatchingMetric> matchingMetrics = matchMetrics(names); 
		return matchingMetrics.size();
	}
}
