package org.metricssampler.reader;

/**
 * A reader that can only read one single metric from an input at a time. 
 */
public interface MetaDataMetricsReader extends MetricsReader {
	MetricsMetaData getMetaData() throws MetricReadException;
	MetricValue readMetric(MetricName metric) throws MetricReadException;
}
