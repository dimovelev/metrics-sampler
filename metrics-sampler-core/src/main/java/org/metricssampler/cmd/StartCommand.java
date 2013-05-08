package org.metricssampler.cmd;

import org.metricssampler.daemon.Daemon;
import org.metricssampler.service.Bootstrapper;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="start", separators = "=", commandDescription = "Starts the application as a daemon in the background.")
public class StartCommand extends NormalCommand {
	public StartCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		final Daemon daemon = new Daemon(bootstrapper);
		daemon.start();
	}
}
