package org.jmxsampler.extensions.graphite.service;

import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.extensions.graphite.writer.GraphiteMetricsWriter;
import org.jmxsampler.extensions.graphite.writer.GraphiteWriterConfig;
import org.jmxsampler.service.AbstractLocalObjectFactory;
import org.jmxsampler.writer.MetricsWriter;

public class GraphiteObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsWriter(final WriterConfig config) {
		return config instanceof GraphiteWriterConfig;
	}

	@Override
	protected MetricsWriter doNewWriter(final WriterConfig config) {
		return new GraphiteMetricsWriter((GraphiteWriterConfig) config);
	}
}
