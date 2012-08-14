package org.jmxsampler.reader;


public interface MetaDataMetricsReader extends MetricsReader {
	MetricValue readMetric(MetricName metric) throws MetricReadException;
}
