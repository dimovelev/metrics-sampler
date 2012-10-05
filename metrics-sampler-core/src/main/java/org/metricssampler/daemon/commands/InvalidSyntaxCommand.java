package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class InvalidSyntaxCommand extends BaseControlCommand {
	private final String line;
	private final String msg;

	protected InvalidSyntaxCommand(final BufferedReader reader, final BufferedWriter writer, final String line, final String msg) {
		super(reader, writer);
		this.line = line;
		this.msg = msg;
	}

	@Override
	public void execute() throws IOException {
		respond("Line \"" + line + "\" is invalid: " + msg);
	}

}
