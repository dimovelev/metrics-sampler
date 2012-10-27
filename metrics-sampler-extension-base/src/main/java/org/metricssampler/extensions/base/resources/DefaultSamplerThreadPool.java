package org.metricssampler.extensions.base.resources;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.metricssampler.config.ThreadPoolConfig;
import org.metricssampler.resources.SamplerThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSamplerThreadPool implements SamplerThreadPool {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ThreadPoolConfig config;
	private final ScheduledExecutorService executorService;
	
	public DefaultSamplerThreadPool(final ThreadPoolConfig config) {
		checkArgumentNotNull(config, "config");
		this.config = config;
		this.executorService = createExecutorService(config);
	}

	private ScheduledThreadPoolExecutor createExecutorService(final ThreadPoolConfig config) {
		logger.info("Starting scheduled thread pool \"{}\" with {} threads", config.getName(), config.getSize());
		return new ScheduledThreadPoolExecutor(config.getSize());
	}

	@Override
	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	@Override
	public void shutdown() {
		logger.info("Shutting down thread pool {}", config.getName());
		try {
			logger.debug("Waiting for the executor service to gracefully shutdown");
			executorService.awaitTermination(20, TimeUnit.SECONDS);
			logger.info("Executor service terminated");
		} catch (final InterruptedException e) {
			logger.warn("Thread pool failed to gracefully shutdown within 20 seconds. Forcing shtudown");
		}
	}
}
