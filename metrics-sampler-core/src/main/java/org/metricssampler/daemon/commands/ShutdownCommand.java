package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

public class ShutdownCommand extends BaseControlCommand {
	private final ExecutorService executor;

	protected ShutdownCommand(final BufferedReader reader, final BufferedWriter writer, final ExecutorService executor) {
		super(reader, writer);
		this.executor = executor;
	}

	@Override
	public void execute() {
		logger.info("Shutdown command received");
		logger.info("Shutting down executor service");
		executor.shutdown();
		try {
			logger.debug("Waiting for the executor service to gracefully shutdown");
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			logger.warn("Thread pool failed to gracefully shutdown within 20 seconds. Forcing shtudown");
		}
		logger.info("Executor service terminated");
		IOUtils.closeQuietly(writer);
		System.exit(0);
	}

}
