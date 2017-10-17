package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.sampler.Sampler;

@Parameters(commandNames="check", commandDescriptionKey="help.check.command")
public class CheckCommand extends SamplersCommand {
	private boolean allValid;
	
	@Override
	protected void preProcess() {
		allValid = true;
	}

	@Override
	protected void process(final Sampler sampler) {
		logger.info("Checking {}", sampler);
		try {
			final boolean valid = sampler.check();
			allValid = allValid && valid;
		} catch (final MetricReadException e) {
			logger.warn("Sampler threw exception during check", e);
			allValid = false;
		}
	}


	@Override
	protected void postProcess() {
		if (allValid) {
			logger.info("Everything looks alright");
		} else {
			logger.info("There were problems. See the logs.");
		}
	}

}
