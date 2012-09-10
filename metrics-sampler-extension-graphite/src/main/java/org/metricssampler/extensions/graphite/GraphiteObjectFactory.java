package org.metricssampler.extensions.graphite;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.service.AbstractLocalObjectFactory;
import org.metricssampler.writer.MetricsWriter;

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
