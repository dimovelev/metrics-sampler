package org.metricssampler.writer;

import org.metricssampler.reader.Metrics;

public interface MetricsWriter {
	void open() throws MetricWriteException;
	void close();

	void write(Metrics metrics) throws MetricWriteException;
}
