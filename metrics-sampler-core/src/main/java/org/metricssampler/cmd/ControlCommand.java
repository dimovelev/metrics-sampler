package org.metricssampler.cmd;

import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

import com.beust.jcommander.ParametersDelegate;

/**
 * Base class for control commands that only require the control host and port to operate.
 */
public abstract class ControlCommand extends BootstrappedCommand {
	@ParametersDelegate
	private ControlCommandDelegate control = new ControlCommandDelegate();
	
	@Override
	protected Bootstrapper createBootstrapper() {
		return DefaultBootstrapper.bootstrap(control.getHost(), control.getPort());
	}
}
