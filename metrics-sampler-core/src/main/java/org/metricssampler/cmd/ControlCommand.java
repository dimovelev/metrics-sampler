package org.metricssampler.cmd;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class ControlCommand extends AbstractCommand {
	public ControlCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	public void run() {
		final Bootstrapper bootstrapper = DefaultBootstrapper.bootstrap(getControlHost(), getControlPort());
		runControl(bootstrapper);
	}
	
	protected abstract void runControl(final Bootstrapper bootstrapper);

}
