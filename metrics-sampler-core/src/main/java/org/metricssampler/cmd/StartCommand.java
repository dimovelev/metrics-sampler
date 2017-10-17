package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import org.metricssampler.daemon.Daemon;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

@Parameters(commandNames="start", commandDescriptionKey="help.start.command")
public class StartCommand extends BootstrappedCommand {
	@ParametersDelegate
	private ConfigurationCommandDelegate configuration = new ConfigurationCommandDelegate();

	@ParametersDelegate
	private ControlCommandDelegate control = new ControlCommandDelegate();
	
	public StartCommand() {
		logbackConfig = "config/logback.xml";
	}

	@Override
	protected Bootstrapper createBootstrapper() {
		return DefaultBootstrapper.bootstrap(configuration.getConfig(), control.getHost(), control.getPort());
	}

	@Override
	protected void runBootstrapped() {
		final Daemon daemon = new Daemon(bootstrapper);
		daemon.start();
	}
}
