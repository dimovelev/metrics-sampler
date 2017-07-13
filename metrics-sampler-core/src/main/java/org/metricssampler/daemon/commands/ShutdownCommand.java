package org.metricssampler.daemon.commands;

import org.metricssampler.service.Bootstrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class ShutdownCommand extends BaseControlCommand {
	private final Bootstrapper bootstrapper;

	protected ShutdownCommand(final BufferedReader reader, final BufferedWriter writer, final Bootstrapper bootstrapper) {
		super(reader, writer);
		this.bootstrapper = bootstrapper;
	}

	@Override
	public void execute() {
		logger.info("Shutdown command received");
		logger.info("Shutting down thread pools");
		bootstrapper.shutdown();
		closeQuietly(writer);
		System.exit(0);
	}

}
