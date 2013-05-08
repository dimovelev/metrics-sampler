package org.metricssampler.cmd;

import java.util.LinkedList;
import java.util.List;

import org.metricssampler.sampler.Sampler;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="test", separators = "=", commandDescription = "Calls all enabled samplers once and exits.")
public class TestCommand extends SamplersCommand {
	@Parameter(description = "<name>* - The names of the samplers to test. Leave empty for all.")
	private List<String> samplers = new LinkedList<String>();

	public TestCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void process(final Sampler sampler) {
		logger.info("Testing {}", sampler);
		sampler.sample();
	}
}
