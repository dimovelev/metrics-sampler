package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

@Parameters(commandNames="sampler", commandDescriptionKey="help.sampler.command")
public class SamplerCommand extends ControlCommand {
	@Parameter(names = "-n", descriptionKey="help.param.sampler.samplers", required=true)
	protected List<String> samplers = new LinkedList<String>();

	@Parameter(names = "-a", descriptionKey="help.param.sampler.action", required=true)
	protected String action;

	@Override
	protected void runBootstrapped() {
		for (final String sampler : samplers) {
			final String prefix = action + " sampler \"" + sampler + "\": ";
			try {
				final String response = execute(bootstrapper.getControlHost(), bootstrapper.getControlPort(), "sampler " + sampler + " " + action);
				System.out.println(prefix + response);
			} catch (final UnknownHostException e) {
				System.err.println(prefix + e.getMessage());
			} catch (final IOException e) {
				System.err.println(prefix + e.getMessage());
			}
		}
	}

}
