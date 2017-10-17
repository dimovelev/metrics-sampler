package org.metricssampler.cmd;

import com.beust.jcommander.ParametersDelegate;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

/**
 * Base class for commands that require the loaded application configuration.
 */
public abstract class ConfigurationCommand extends BootstrappedCommand {
	@ParametersDelegate
	protected ConfigurationCommandDelegate configuration = new ConfigurationCommandDelegate();

	@Override
	protected Bootstrapper createBootstrapper() {
		return DefaultBootstrapper.bootstrap(configuration.getConfig(), false);
	}
}
