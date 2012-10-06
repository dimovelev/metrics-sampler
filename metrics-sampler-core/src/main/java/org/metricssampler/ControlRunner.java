package org.metricssampler;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class ControlRunner implements Runner {
	@Override
	public void run(final String... args) {
		final Bootstrapper bootstrapper = DefaultBootstrapper.bootstrap();
		runControl(bootstrapper.getControlHost(), bootstrapper.getControlPort(), args);
	}
	
	protected abstract void runControl(String host, int port, String... args);
}
