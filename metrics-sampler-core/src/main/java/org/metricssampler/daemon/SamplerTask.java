package org.metricssampler.daemon;

import org.metricssampler.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplerTask implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private volatile boolean enabled = true;
	private final Sampler sampler;
	
	public SamplerTask(final Sampler sampler) {
		this.sampler = sampler;
	}

	@Override
	public void run() {
		if (enabled) {
			try {
				sampler.sample();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring.", e);
			}
		}
	}

	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
}
