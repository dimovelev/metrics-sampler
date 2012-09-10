package org.metricssampler.extensions.base.selector.regexp;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetaDataMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.MetricsMetaData;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.selector.PlaceholderReplacer;
import org.metricssampler.selector.SelectedMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExpMetricsSelector implements MetricsSelector {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final PlaceholderReplacer placeholderReplacer = new PlaceholderReplacer();
	
	private final RegExpSelectorConfig config;
	private Map<String, Object> placeholders;

	private MetricsMetaData cachedMetaData;
	private List<SelectedMetric> cachedSelectedMetrics;
	
	public RegExpMetricsSelector(final RegExpSelectorConfig config) {
		this.config = config;
	}

	@Override
	public Map<String, MetricValue> readMetrics(final MetricsReader reader) {
		if (reader instanceof MetaDataMetricsReader) {
			return readAlreadySelected((MetaDataMetricsReader) reader);
		} else if (reader instanceof BulkMetricsReader) {
			return readAllAndSelect((BulkMetricsReader) reader);
		} else {
			throw new IllegalArgumentException("Unsupported reader: " + reader);
		}
	}
	
	protected Map<String, MetricValue> readAllAndSelect(final BulkMetricsReader reader) {
		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		final Map<MetricName, MetricValue> metrics = reader.readAllMetrics();
		for (final Map.Entry<MetricName, MetricValue> entry : metrics.entrySet()) {
			final SelectedMetric metric = match(entry.getKey());
			if (metric != null) {
				result.put(metric.getName(), entry.getValue());
			}
		}
		return result;
	}
	
	protected Map<String, MetricValue> readAlreadySelected(final MetaDataMetricsReader reader) {
		final List<SelectedMetric> matchingMetrics = getSelectedMetrics(reader);
		final Map<String, MetricValue> result = new HashMap<String, MetricValue>();
		for (final SelectedMetric bean : matchingMetrics) {
			try {
				final MetricValue value = reader.readMetric(bean.getOriginalName());
				result.put(bean.getName(), value);
			} catch (final MetricReadException e) {
				logger.warn("Failed to read " + bean.getOriginalName(), e);
			}
		}
		return result;
	}

	protected void assertSelectedMetrics() {
		if (cachedSelectedMetrics == null) {
			throw new IllegalStateException("setMetaData not called");
		}
	}

	
	@Override
	public void setPlaceholders(final Map<String, Object> readerContext) {
		this.placeholders = readerContext;
	}

	private List<SelectedMetric> getSelectedMetrics(final MetaDataMetricsReader reader) {
		final MetricsMetaData metaData = reader.getMetaData();
		if (this.cachedMetaData != metaData) {
			this.cachedSelectedMetrics = selectMetrics(metaData);
			if (cachedSelectedMetrics.isEmpty()) {
				logger.warn(this + " matched no metrics");
			}
		}
		return cachedSelectedMetrics;
	}

	private List<SelectedMetric> selectMetrics(final Iterable<MetricName> names) {
		final List<SelectedMetric> result = new LinkedList<SelectedMetric>();
		for (final MetricName name : names) {
			final SelectedMetric metric = match(name);
			if (metric != null) {
				result.add(metric);
			}
		}
		return Collections.unmodifiableList(result);
	}

	protected SelectedMetric match(final MetricName from) {
		Map<String, Object> context = null;

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
		context.putAll(placeholders);
		final String newName = placeholderReplacer.replacePlaceholders(config.getKeyExpression(), context);
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
	public int getMetricCount(final MetricsReader reader) {
		Iterable<MetricName> names;
		if (reader instanceof MetaDataMetricsReader) {
			names = ((MetaDataMetricsReader) reader).getMetaData();
		} else if (reader instanceof BulkMetricsReader) {
			names = ((BulkMetricsReader) reader).readAllMetrics().keySet();
		} else {
			throw new IllegalArgumentException("Unsupported metrics reader: " + reader);
		}
		final List<SelectedMetric> matchingMetrics = selectMetrics(names); 
		return matchingMetrics.size();
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
}
