package org.jmxsampler.reader;

import java.util.Collection;
import java.util.Map;

public interface MetricsReader {
	void open() throws MetricReadException;
	void close();

	Collection<SourceMetricMetaData> getMetaData() throws MetricReadException;
	Map<String, String> getTransformationContext();
	MetricValue readMetric(SourceMetricMetaData metric) throws MetricReadException;

	void addListener(MetricReaderListener listener);
	Map<SourceMetricMetaData, MetricValue> readAllMetrics() throws MetricReadException;
}
