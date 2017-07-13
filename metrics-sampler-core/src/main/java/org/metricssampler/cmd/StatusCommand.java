package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.net.ConnectException;

@Parameters(commandNames="status", commandDescriptionKey="help.status.command")
public class StatusCommand extends ControlCommand {
	@Override
	protected void runBootstrapped() {
		final String msg = checkStatus(bootstrapper.getControlHost(), bootstrapper.getControlPort());
		System.out.println(msg);
		System.exit(msg.startsWith("Running [") ? 0 : 1);
	}

	protected String checkStatus(final String host, final int port) {
		try {
			final String response = execute(host, port, "status").trim();
			if ("ok".equals(response)) {
				return "Running [port " + port + "]";
			} else {
				return "Running on control port " + port + " but responded with: \"" + response + "\"";
			}
		} catch (final ConnectException e) {
			return "Stopped";
		} catch (final IOException e) {
			return "Unknown state: " + e.getMessage();
		}
	}
}
