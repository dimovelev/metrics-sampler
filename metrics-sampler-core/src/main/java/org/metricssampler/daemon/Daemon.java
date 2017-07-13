package org.metricssampler.daemon;

import org.metricssampler.config.SamplerConfig;
import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SamplerThreadPool;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Daemon {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Bootstrapper bootstrapper;

	private Thread controllerThread;
	private final Map<String, SamplerTask> tasks = new HashMap<>();

	public Daemon(final Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	/**
	 * The order of operations is significant here:
	 * <ol>
	 * <li>Setup the thread pool</li>
	 * <li>Create the controller which makes sure there is no other process running on the
	 * local machine because it would otherwise fail to bind the server socket</li>
	 * <li>Schedule the samplers</li>
	 * <li>Startup the controller thread. From this point it can really process requests</li>
	 * </ol>
	 */
	public void start() {
		createController();
		scheduleSamplers();
		controllerThread.start();
	}

	private void createController() {
		try {
			final Runnable controller = new DefaultTCPController(bootstrapper, tasks, bootstrapper.getSharedResources());
			controllerThread = new Thread(controller);
		} catch (final IllegalStateException e) {
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	private void scheduleSamplers() {
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			final SamplerConfig config = sampler.getConfig();
			logger.info("Scheduling {} at fixed rate of {} seconds", sampler, config.getInterval());
			final SamplerThreadPool threadPool = (SamplerThreadPool) bootstrapper.getSharedResource(sampler.getConfig().getPool());
			final SamplerTask task = threadPool.schedule(sampler);
			tasks.put(config.getName(), task);
		}
	}
}
