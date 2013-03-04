package org.metricssampler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class NormalRunner implements Runner {

	@Override
	public void run(final String... args) {
		if (args.length != 1) {
			outputHelp();
		}
		final File configFile = new File(args[0]);
		if (!configFile.canRead()) {
			System.err.println("Configuration file " + configFile.getAbsolutePath() + " not readable");
			System.exit(2);
		}
		final Bootstrapper bootstrapper = DefaultBootstrapper.bootstrap(configFile.getAbsolutePath());
		try {
			runBootstrapped(bootstrapper);
		} catch (final Exception e) {
			System.err.println("Exception raised during bootstrapping. Check out the logs for more information. Message: " + e.getMessage());
			System.exit(3);
		}
	}

	private void outputHelp() {
		try {
			final String usage = IOUtils.toString(getClass().getResource("usage.txt"));
			System.out.println(usage);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}

	protected abstract void runBootstrapped(Bootstrapper bootstrapper);

}
