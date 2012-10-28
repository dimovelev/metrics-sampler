package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.service.Bootstrapper;

public class BootstrapperTest extends BootstrapperTestBase {

	@Test(expected=ConfigurationException.class)
	public void bootstrapNonExistentFile() {
		bootstrap("I_DONT_EXIST");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapUnknownElement() {
		bootstrap("invalid.xml");
	}
	
	@Test
	public void bootstrapMinimal() {
		final Bootstrapper result = bootstrap("minimal.xml");

		final Configuration config = result.getConfiguration();
		assertNotNull(config);
		assertTrue(config.getInputs().isEmpty());
		assertTrue(config.getOutputs().isEmpty());
		assertTrue(config.getSamplers().isEmpty());
		assertTrue(config.getSharedResources().isEmpty());
		assertTrue(config.getVariables().isEmpty());
	}
}
