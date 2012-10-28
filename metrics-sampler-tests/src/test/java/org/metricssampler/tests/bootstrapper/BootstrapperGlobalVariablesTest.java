package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.service.Bootstrapper;

public class BootstrapperGlobalVariablesTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Bootstrapper result = bootstrap("global-variables/complete.xml");
		
		final Configuration config = result.getConfiguration();
		assertNotNull(config);
		assertSingleStringVariable(config.getVariables(), "string", "value");
	}

	@Test(expected=ConfigurationException.class)
	public void bootstrapDuplicateName() {
		bootstrap("global-variables/duplicate-name.xml");
	}
}
