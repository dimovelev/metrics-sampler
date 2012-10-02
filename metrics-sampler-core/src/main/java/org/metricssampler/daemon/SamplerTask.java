package org.metricssampler.daemon;

import org.metricssampler.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SamplerTask implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private volatile boolean enabled = true;
	private volatile boolean enableOnce = false;
	private final Sampler sampler;
	
	public SamplerTask(final Sampler sampler) {
		this.sampler = sampler;
	}

	@Override
	public void run() {
		MDC.put("sampler", sampler.getConfig().getName());
		if (enabled || enableOnce) {
			try {
				sampler.sample();
				enableOnce = false;
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring.", e);
			}
		} else {
			logger.debug("Sampler disabled thus not sampling");
		}
		MDC.remove("sampler");
	}

	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void enableOnce() {
		enableOnce = true;
	}
	
	public String getName() {
		return sampler.getConfig().getName();
	}
}
