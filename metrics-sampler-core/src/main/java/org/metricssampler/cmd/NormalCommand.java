package org.metricssampler.cmd;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class NormalCommand extends AbstractCommand {

	public NormalCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	public void run() {
		final Bootstrapper bootstrapper = DefaultBootstrapper.bootstrap(getConfig(), getControlHost(), getControlPort());
		try {
			runBootstrapped(bootstrapper);
		} catch (final Exception e) {
			System.err.println("Exception raised during bootstrapping. Check out the logs for more information. Message: " + e.getMessage());
			System.exit(3);
		}
	}

	protected abstract void runBootstrapped(Bootstrapper bootstrapper);

}
