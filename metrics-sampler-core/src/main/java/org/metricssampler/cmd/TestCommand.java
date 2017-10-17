package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;
import org.metricssampler.sampler.Sampler;

@Parameters(commandNames="test", commandDescriptionKey="help.test.command")
public class TestCommand extends SamplersCommand {
	@Override
	protected void process(final Sampler sampler) {
		logger.info("Testing {}", sampler);
		sampler.sample();
	}
}
