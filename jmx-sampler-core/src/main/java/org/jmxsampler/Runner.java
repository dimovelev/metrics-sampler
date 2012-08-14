package org.jmxsampler;

import java.io.File;

import org.jmxsampler.config.Configuration;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.reader.MetricName;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.service.ExtensionsRegistry;
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
			System.err.println("Configuartion file "+configFile.getAbsolutePath()+" not readable");
			System.exit(2);
		}

		final ExtensionsRegistry registry = new ExtensionsRegistry();
		final Configuration config = registry.newConfigurationLoader().load(configFile.getAbsolutePath());

		executeCommand(command, registry, config);
	}

	private static void outputHelp() {
		System.err.println("Usage: <jmx-sampler> [start|check|metadata] <config.xml>");
		System.err.println();
		System.err.println("\tstart     start the sampler with the given configuration");
		System.err.println("\tcheck     check whether each transformation rule matches at least one metric");
		System.err.println("\tmetadata  dump metadata from all readers (e.g. list all bean and attributes for a JMX reader)");
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
		for(final ReaderConfig readerConfig : config.getReaders()) {
			final MetricsReader reader = registry.newReader(readerConfig);
			reader.open();
			System.out.println("Reader: " + readerConfig.getName());
			for(final MetricName item : reader.getMetaData()) {
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
