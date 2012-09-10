package org.metricssampler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.ExtensionsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {
	private static final Logger logger = LoggerFactory.getLogger(Runner.class);

	public enum Command {
		START,
		CHECK,
		METADATA
	}

	public static void main(final String[] args) {
		if (args.length != 2) {
			outputHelp();
		}
		final Command command = Command.valueOf(args[0].toUpperCase());
		final File configFile = new File(args[1]);
		if (!configFile.canRead()) {
			System.err.println("Configuartion file " + configFile.getAbsolutePath() + " not readable");
			System.exit(2);
		}

		final ExtensionsRegistry registry = new ExtensionsRegistry();
		final Configuration config = registry.newConfigurationLoader().load(configFile.getAbsolutePath());

		executeCommand(command, registry, config);
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

	private static void executeCommand(final Command command, final ExtensionsRegistry registry, final Configuration config) {
		switch (command) {
			case START:
				executeStart(registry, config);
				break;
			case CHECK:
				executeCheck(registry, config);
				break;
			case METADATA:
				executeMetadata(registry, config);
				break;
		}
	}

	private static void executeMetadata(final ExtensionsRegistry registry, final Configuration config) {
		for(final InputConfig input : config.getInputs()) {
			final MetricsReader reader = registry.newReaderForInput(input);
			reader.open();
			System.out.println("Reader: " + input.getName());
			for(final MetricName item : reader.readNames()) {
				System.out.println("\tName:" + item.getName());
				System.out.println("\tDescription:" + item.getDescription());
			}
			reader.close();
		}
	}

	private static void executeCheck(final ExtensionsRegistry registry, final Configuration config) {
		boolean allValid = true;
		for (final SamplerConfig samplerConfig : config.getSamplers()) {
			if (!samplerConfig.isDisabled()) {
				final Sampler sampler = registry.newSampler(samplerConfig);
				logger.info("Checking "+sampler);
				try { 
					final boolean valid = sampler.check();
					allValid = allValid && valid;
				} catch (final MetricReadException e) {
					logger.warn("Sampler threw exception during check", e);
					allValid = false;
				}
			} else {
				logger.info(samplerConfig + " is disabled and will not be checked");
			}
		}
		if (allValid) {
			logger.info("Everything looks alright");
		} else {
			logger.info("There were problems. See the logs.");
		}
	}

	private static void executeStart(final ExtensionsRegistry registry, final Configuration config) {
		final Daemon daemon = new Daemon(config, registry);
		daemon.start();
	}
}
