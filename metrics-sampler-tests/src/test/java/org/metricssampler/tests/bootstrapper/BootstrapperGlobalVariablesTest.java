package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;

public class BootstrapperGlobalVariablesTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("global-variables/complete.xml");
		
		assertSingleStringVariable(config.getVariables(), "string", "value");
	}

	@Test(expected=ConfigurationException.class)
	public void bootstrapDuplicateName() {
		configure("global-variables/duplicate-name.xml");
	}
}
