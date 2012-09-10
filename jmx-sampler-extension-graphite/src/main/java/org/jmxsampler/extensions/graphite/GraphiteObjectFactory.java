package org.jmxsampler.extensions.graphite;

import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.service.AbstractLocalObjectFactory;
import org.jmxsampler.writer.MetricsWriter;

public class GraphiteObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsOutput(final OutputConfig config) {
		return config instanceof GraphiteOutputConfig;
	}

	@Override
	protected MetricsWriter doNewWriter(final OutputConfig config) {
		return new GraphiteMetricsWriter((GraphiteOutputConfig) config);
	}
}
