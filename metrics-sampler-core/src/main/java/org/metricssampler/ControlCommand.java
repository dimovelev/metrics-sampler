package org.metricssampler;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class ControlCommand {
	private final Bootstrapper bootstrapper;
	
	public ControlCommand() {
		bootstrapper = DefaultBootstrapper.bootstrap();
	}
	
	public void process(final String... args) {
		process(bootstrapper.getControlHost(), bootstrapper.getControlPort(), args);
	}
	
	protected abstract void process(String host, int port, String... args);
}
