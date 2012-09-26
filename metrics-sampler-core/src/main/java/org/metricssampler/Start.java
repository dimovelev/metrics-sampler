package org.metricssampler;

import org.metricssampler.daemon.Daemon;
import org.metricssampler.service.Bootstrapper;

public class Start extends NormalRunner {

	public static void main(final String[] args) {
		new Start().run(args);
	}

	@Override
	protected void run(final Bootstrapper bootstrapper) {
		final Daemon daemon = new Daemon(bootstrapper);
		daemon.start();
	}

}
