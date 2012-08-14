package org.jmxsampler.reader;

import java.util.Collection;
import java.util.Map;

public interface MetricsReader {
	void open() throws MetricReadException;
	void close();

	Collection<MetricName> getMetaData() throws MetricReadException;
	Map<String, String> getTransformationContext();
	MetricValue readMetric(MetricName metric) throws MetricReadException;

	void addListener(MetricReaderListener listener);
	Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException;
}
