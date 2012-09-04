package org.jmxsampler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jmxsampler.config.Configuration;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.service.ExtensionsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Daemon {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Configuration config;
	private final ExtensionsRegistry registry;
	private ScheduledThreadPoolExecutor executor;

	public Daemon(final Configuration config, final ExtensionsRegistry registry) {
		this.registry = registry;
		this.config = config;
	}

	public void start() {
		logger.info("Starting thread pool executor with thread pool size: "+config.getPoolSize());
        executor = new ScheduledThreadPoolExecutor(config.getPoolSize());

		for (final SamplerConfig samplerConfig : config.getSamplers()) {
			final Sampler sampler = registry.newSampler(samplerConfig);
			if (samplerConfig.isDisabled()) {
				logger.info(sampler + " is disabled and will not be scheduled");
				continue;
			}
			logger.info("Scheduling {} at fixed rate of {} seconds", sampler, samplerConfig.getInterval());
	        executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					logger.debug("Sampling");
					try {
						sampler.sample();
					} catch (final RuntimeException e) {
						logger.warn("Sampler threw exception. Ignoring", e);
					}
				}
			}, 0L, samplerConfig.getInterval(), TimeUnit.SECONDS);
		}
	}
}
