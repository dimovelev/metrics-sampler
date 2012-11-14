package org.metric.sampler.extension.redis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

public class RedisExtension extends AbstractExtension {

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(RedisInputXBean.class);
		result.add(RedisCommandXBean.class);
		result.add(RedisHLenCommandXBean.class);
		result.add(RedisLLenCommandXBean.class);
		return result;
	}

	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof RedisInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		final RedisInputConfig typedConfig = (RedisInputConfig) config;
		return new RedisMetricsReader(typedConfig);
	}

}
