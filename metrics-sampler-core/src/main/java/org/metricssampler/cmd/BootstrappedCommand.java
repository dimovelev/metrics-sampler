package org.metricssampler.cmd;

import org.metricssampler.service.Bootstrapper;

/**
 * Base classes for command that require a bootstrapped environment to operate
 */
public abstract class BootstrappedCommand extends AbstractCommand {
	protected Bootstrapper bootstrapper;
	
	@Override
	public void run() {
		try {
			bootstrapper = createBootstrapper();
		} catch (final Exception e) {
			System.err.println("Exception raised during bootstrapping. Check out the logs for more information. Message: " + e.getMessage());
			System.exit(3);
		}
		runBootstrapped();
	}

	protected abstract Bootstrapper createBootstrapper();

	protected abstract void runBootstrapped();
}
