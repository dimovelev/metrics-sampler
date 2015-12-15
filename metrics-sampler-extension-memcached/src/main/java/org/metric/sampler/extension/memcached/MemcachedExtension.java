package org.metric.sampler.extension.memcached;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MemcachedExtension extends AbstractExtension {

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<>();
		result.add(MemcachedInputXBean.class);
		return result;
	}

	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof MemcachedInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new MemcachedMetricsReader((MemcachedInputConfig) config);
	}

}
