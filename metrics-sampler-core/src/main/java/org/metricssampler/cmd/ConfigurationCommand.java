package org.metricssampler.cmd;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

import com.beust.jcommander.ParametersDelegate;

/**
 * Base class for commands that require the loaded application configuration.
 */
public abstract class ConfigurationCommand extends BootstrappedCommand {
	@ParametersDelegate
	protected ConfigurationCommandDelegate configuration = new ConfigurationCommandDelegate();

	@Override
	protected Bootstrapper createBootstrapper() {
		return DefaultBootstrapper.bootstrap(configuration.getConfig());
	}
}
