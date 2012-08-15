package org.jmxsampler.reader;

/**
 * A reader that can only fetch one single metric at a time. 
 */
public interface MetaDataMetricsReader extends MetricsReader {
	MetricsMetaData getMetaData() throws MetricReadException;
	MetricValue readMetric(MetricName metric) throws MetricReadException;
}
