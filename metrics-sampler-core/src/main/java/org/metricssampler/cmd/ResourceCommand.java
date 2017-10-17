package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

@Parameters(commandNames="resource", commandDescriptionKey="help.resource.command")
public class ResourceCommand extends ControlCommand {
	@Parameter(names = "-n", descriptionKey="help.param.resource.names", required=true)
	protected List<String> resources = new LinkedList<String>();

	@Parameter(names = "-a", descriptionKey="help.param.resource.action", required=true)
	protected String action;

	@Override
	protected void runBootstrapped() {
		for (final String resource : resources) {
			final String prefix = action + " resource \"" + resource + "\": ";
			try {
				final String response = execute(bootstrapper.getControlHost(), bootstrapper.getControlPort(), "resource " + resource + " " + action);
				System.out.println(prefix + response);
			} catch (final UnknownHostException e) {
				System.err.println(prefix + e.getMessage());
			} catch (final IOException e) {
				System.err.println(prefix + e.getMessage());
			}
		}
	}

}
