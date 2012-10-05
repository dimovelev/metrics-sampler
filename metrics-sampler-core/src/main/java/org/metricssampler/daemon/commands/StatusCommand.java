package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class StatusCommand extends BaseControlCommand {
	protected StatusCommand(final BufferedReader reader, final BufferedWriter writer) {
		super(reader, writer);
	}

	@Override
	public void execute() throws IOException {
		logger.debug("Status command received. Responding with ok.");
		respond("ok");
	}
}
