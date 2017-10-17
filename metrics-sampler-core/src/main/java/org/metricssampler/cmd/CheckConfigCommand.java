package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

@Parameters(commandNames="check-config", commandDescriptionKey="help.check-config.command")
public class CheckConfigCommand extends ConfigurationCommand {
	@Override
	protected Bootstrapper createBootstrapper() {
		return DefaultBootstrapper.bootstrap(configuration.getConfig(), true);
	}

	@Override
	protected void runBootstrapped() {
		logger.info("Configuration looks good");
	}
}
