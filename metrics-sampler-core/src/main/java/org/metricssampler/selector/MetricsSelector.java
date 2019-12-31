package org.metricssampler.selector;

import java.util.Map;

import org.metricssampler.reader.Metrics;
import org.metricssampler.reader.MetricsReader;

/**
 * Select metrics to forward to the outputs, optionally renaming them.
 */
public interface MetricsSelector {
    /**
     * @param reader the metrics reader
     * @return fetch matching metrics from the reader.
     */
    Metrics readMetrics(MetricsReader reader);

    void setVariables(Map<String, Object> variables);

    /**
     * @param reader the metrics reader
     * @return the number of matching metrics
     */
    int getMetricCount(MetricsReader reader);

    void reset();
}
