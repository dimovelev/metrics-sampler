package org.metricssampler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;
import org.metricssampler.service.GlobalObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point for the application
 */
public class Runner {
	private static final Logger logger = LoggerFactory.getLogger(Runner.class);

	public enum Command {
		/**
		 * Start the application and sample forever
		 */
		START,
		
		/**
		 * Stop the application
		 */
		STOP,
		
		/**
		 * Check the configuration by fetching metrics from every enabled sampler and check whether each selector returns at least one metric.
		 */
		CHECK,
		
		/**
		 * Query the metadata of each enabled sampler and output it. This is useful to know what metrics are available so that you can define your selector regexps. 
		 */
		METADATA,
		
		/**
		 * Fetch metrics from each enabled sampler and output them once.
		 */
		TEST
	}

	public static void main(final String[] args) {
		if (args.length != 2) {
			outputHelp();
		}
		final Command command = Command.valueOf(args[0].toUpperCase());
		final File configFile = new File(args[1]);
		if (!configFile.canRead()) {
			System.err.println("Configuration file " + configFile.getAbsolutePath() + " not readable");
			System.exit(2);
		}

		final Bootstrapper bootrapper = DefaultBootstrapper.bootstrap(configFile.getAbsolutePath());

		executeCommand(command, bootrapper);
	}

	private static void outputHelp() {
		try {
			final String usage = IOUtils.toString(Runner.class.getResource("usage.txt"));
			System.out.println(usage);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}

	private static void executeCommand(final Command command, final Bootstrapper bootstrapper) {
		switch (command) {
			case START:
				executeStart(bootstrapper);
				break;
			case STOP:
				executeStop(bootstrapper);
				break;
			case CHECK:
				executeCheck(bootstrapper);
				break;
			case METADATA:
				executeMetadata(bootstrapper);
				break;
			case TEST:
				executeTest(bootstrapper);
				break;
		}
	}

	private static void executeStart(final Bootstrapper bootstrapper) {
		final Daemon daemon = new Daemon(bootstrapper);
		daemon.start();
	}

	private static void executeStop(final Bootstrapper bootstrapper) {
		try {
			final Socket socket = new Socket(bootstrapper.getShutdownHost(), bootstrapper.getShutdownPort());
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("shutdown\n");
			writer.close();
			socket.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void executeCheck(final Bootstrapper bootstrapper) {
		boolean allValid = true;
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			logger.info("Checking {}", sampler);
			try { 
				final boolean valid = sampler.check();
				allValid = allValid && valid;
			} catch (final MetricReadException e) {
				logger.warn("Sampler threw exception during check", e);
				allValid = false;
			}
		}
		if (allValid) {
			logger.info("Everything looks alright");
		} else {
			logger.info("There were problems. See the logs.");
		}
	}

	private static void executeMetadata(final Bootstrapper bootstrapper) {
		final GlobalObjectFactory factory = (GlobalObjectFactory) bootstrapper;
		for(final InputConfig input : bootstrapper.getConfiguration().getInputs()) {
			final MetricsReader reader = factory.newReaderForInput(input);
			reader.open();
			System.out.println("Reader: " + input.getName());
			for(final MetricName item : reader.readNames()) {
				System.out.println("\tName:" + item.getName());
				System.out.println("\tDescription:" + item.getDescription());
			}
			reader.close();
		}
	}
	
	private static void executeTest(final Bootstrapper bootstrapper) {
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			try {
				sampler.sample();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring", e);
			}
		}
	}
}
