package org.metricssampler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.metricssampler.config.Configuration;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Daemon {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Bootstrapper bootstrapper;

	public Daemon(final Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	public void start() {
		final ScheduledThreadPoolExecutor executor = setupThreadPool();

		for (final Sampler sampler : bootstrapper.getSamplers()) {
			logger.info("Scheduling {} at fixed rate of {} seconds", sampler, sampler.getRate());
	        executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						sampler.sample();
					} catch (final RuntimeException e) {
						logger.warn("Sampler threw exception. Ignoring.", e);
					}
				}
			}, 0L, sampler.getRate(), TimeUnit.SECONDS);
		}
	}

	private ScheduledThreadPoolExecutor setupThreadPool() {
		final Configuration config = bootstrapper.getConfiguration();
		logger.info("Starting thread pool executor with thread pool size: "+config.getPoolSize());
        return new ScheduledThreadPoolExecutor(config.getPoolSize());
	}
}
