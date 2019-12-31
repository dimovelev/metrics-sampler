package org.metricssampler.selector;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetaDataMetricsReader;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;
import org.metricssampler.reader.MetricsMetaData;
import org.metricssampler.reader.MetricsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A rudimentary implementation of a metrics selector which supports {@link MetaDataMetricsReader} and {@link
 * BulkMetricsReader}.
 */
public abstract class AbstractMetricsSelector implements MetricsSelector {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VariableReplacer variableReplacer = new VariableReplacer();
    private Map<String, Object> variables;
    private MetricsMetaData cachedMetaData;
    private List<SelectedMetric> cachedSelectedMetrics;

    @Override
    public Metrics readMetrics(final MetricsReader reader) {
        if (reader instanceof MetaDataMetricsReader) {
            return readAlreadySelected((MetaDataMetricsReader) reader);
        } else if (reader instanceof BulkMetricsReader) {
            return readAllAndSelect((BulkMetricsReader) reader);
        } else {
            throw new IllegalArgumentException("Unsupported reader: " + reader);
        }
    }

    protected Metrics readAlreadySelected(final MetaDataMetricsReader reader) {
        final List<SelectedMetric> matchingMetrics = getSelectedMetrics(reader);
        final Metrics result = new Metrics();
        for (final SelectedMetric bean : matchingMetrics) {
            try {
                final MetricValue value = reader.readMetric(bean.getOriginalName());
                result.add(bean.getName(), value);
            } catch (final MetricReadException e) {
                logger.warn("Failed to read " + bean.getOriginalName(), e);
            }
        }
        return result;
    }

    protected List<SelectedMetric> getSelectedMetrics(final MetaDataMetricsReader reader) {
        final MetricsMetaData metaData = reader.getMetaData();
        if (this.cachedMetaData != metaData) {
            this.cachedMetaData = metaData;
            this.cachedSelectedMetrics = selectMetrics(metaData);
            if (cachedSelectedMetrics.isEmpty()) {
                logger.warn(this + " matched no metrics");
            }
        }
        return cachedSelectedMetrics;
    }

    protected List<SelectedMetric> selectMetrics(final Iterable<MetricName> names) {
        logger.debug("Selecting metrics");
        final List<SelectedMetric> result = new LinkedList<SelectedMetric>();
        for (final MetricName name : names) {
            final SelectedMetric metric = selectMetric(name);
            if (metric != null) {
                result.add(metric);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Determine whether the metric should be selected or not.
     *
     * @param name the name of the metric
     * @return {@code null} if the metric does not match the selector, the {@link SelectedMetric} otherwise
     */
    protected abstract SelectedMetric selectMetric(MetricName name);

    protected Metrics readAllAndSelect(final BulkMetricsReader reader) {
        final Metrics result = new Metrics();
        final Metrics metrics = reader.readAllMetrics();
        for (final Metric entry : metrics) {
            final SelectedMetric metric = selectMetric(entry.getName());
            if (metric != null) {
                result.add(metric.getName(), entry.getName().getDescription(), entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void setVariables(final Map<String, Object> variables) {
        this.variables = Collections.unmodifiableMap(VariableReplacer.resolve(variables));
        doAfterVariablesSet(variables);
    }

    /**
     * Initialize any fields that depend on the variables
     *
     * @param variables the variable values
     */
    protected abstract void doAfterVariablesSet(Map<String, Object> variables);

    protected String replaceVariables(final String text, final Map<String, Object> variables) {
        return variableReplacer.replaceVariables(text, variables);
    }

    protected String replaceVariables(final String text) {
        return replaceVariables(text, variables);
    }

    @Override
    public int getMetricCount(final MetricsReader reader) {
        Iterable<MetricName> names;
        if (reader instanceof MetaDataMetricsReader) {
            names = ((MetaDataMetricsReader) reader).getMetaData();
        } else if (reader instanceof BulkMetricsReader) {
            names = reader.readNames();
        } else {
            throw new IllegalArgumentException("Unsupported metrics reader: " + reader);
        }
        final List<SelectedMetric> matchingMetrics = selectMetrics(names);
        return matchingMetrics.size();
    }

    protected void addVariables(final Map<String, Object> map) {
        map.putAll(variables);
    }

    @Override
    public void reset() {
        this.cachedMetaData = null;
        this.cachedSelectedMetrics = null;
    }
}
