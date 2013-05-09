package org.metricssampler.cmd;

import org.metricssampler.sampler.Sampler;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="test", commandDescriptionKey="help.test.command")
public class TestCommand extends SamplersCommand {
	@Override
	protected void process(final Sampler sampler) {
		logger.info("Testing {}", sampler);
		sampler.sample();
	}
}
