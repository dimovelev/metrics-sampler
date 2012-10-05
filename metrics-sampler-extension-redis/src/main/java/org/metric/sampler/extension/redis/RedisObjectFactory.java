package org.metric.sampler.extension.redis;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractLocalObjectFactory;

public class RedisObjectFactory extends AbstractLocalObjectFactory {

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
