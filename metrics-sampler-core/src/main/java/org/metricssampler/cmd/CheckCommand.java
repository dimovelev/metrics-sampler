package org.metricssampler.cmd;

import org.metricssampler.reader.MetricReadException;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="check", separators = "=", commandDescription = "Goes through all samplers and checks whether each rule matches at least one metric. Everything is logged to STDOUT.")
public class CheckCommand extends NormalCommand {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public CheckCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		boolean allValid = true;
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			logger.info("Checking {}", sampler);
			try { 
				SamplerStats.init();
				final boolean valid = sampler.check();
				allValid = allValid && valid;
				SamplerStats.unset();
			} catch (final MetricReadException e) {
				logger.warn("Sampler threw exception during check", e);
				allValid = false;
			}
		}
		if (allValid) {
			logger.info("Everything looks alright");
		} else {
			logger.info("There were problems. See the logs.");
		}
	}
}
