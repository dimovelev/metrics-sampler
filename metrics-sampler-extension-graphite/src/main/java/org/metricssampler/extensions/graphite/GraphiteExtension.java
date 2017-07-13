package org.metricssampler.extensions.graphite;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.service.AbstractExtension;
import org.metricssampler.writer.MetricsWriter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GraphiteExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(GraphiteOutputXBean.class);
		return result;
	}
	@Override
	public boolean supportsOutput(final OutputConfig config) {
		return config instanceof GraphiteOutputConfig;
	}

	@Override
	protected MetricsWriter doNewWriter(final OutputConfig config) {
		return new GraphiteMetricsWriter((GraphiteOutputConfig) config);
	}
}
