package org.metricssampler.cmd;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Base classes for command that require a bootstrapped environment to operate
 */
public abstract class BootstrappedCommand extends AbstractCommand {
	protected Bootstrapper bootstrapper;
	
	@Override
	public void run() {
		initLogging();
		
		try {
			bootstrapper = createBootstrapper();
		} catch (final Exception e) {
			System.err.println("Exception raised during bootstrapping. Check out the logs for more information. Message: " + e.getMessage());
			e.printStackTrace();
			System.exit(3);
		}
		runBootstrapped();
	}

	protected void initLogging() {
		final File userOverrideFile = new File(logbackConfig);
		if (userOverrideFile.exists()) {
			configureLogback(userOverrideFile);
		} else {
			final File defaultFile = new File(logbackConfig.replaceAll("\\.xml$", ".default.xml"));
			if (!defaultFile.exists()) {
				System.err.println("Default logback configuration file \"" + defaultFile.getAbsolutePath() + "\" does not exist. Check your installation.");
			}
			configureLogback(defaultFile);
		}
	}

	protected void configureLogback(final File file) {
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory(); 
		final JoranConfigurator jc = new JoranConfigurator(); 
		jc.setContext(context); 
		context.reset(); 
		try {
			jc.doConfigure(file);
		} catch (final JoranException e) {
			e.printStackTrace();
		}
	}

	protected abstract Bootstrapper createBootstrapper();

	protected abstract void runBootstrapped();
}
