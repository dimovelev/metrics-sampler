package org.metricssampler.cmd;

import org.metricssampler.resources.SamplerStats;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="test", separators = "=", commandDescription = "Calls all enabled samplers once and exits.")
public class TestCommand extends NormalCommand {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public TestCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			try {
				SamplerStats.init();
				sampler.sample();
				SamplerStats.unset();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring", e);
			}
		}
	}
}
