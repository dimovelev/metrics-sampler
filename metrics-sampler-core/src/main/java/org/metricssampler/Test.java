package org.metricssampler;

import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test extends NormalRunner {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(final String[] args) {
		new Test().run(args);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			try {
				sampler.sample();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring", e);
			}
		}
	}

}
