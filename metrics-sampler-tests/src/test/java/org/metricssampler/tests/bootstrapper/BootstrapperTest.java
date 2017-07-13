package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;

import static org.junit.Assert.assertTrue;

public class BootstrapperTest extends BootstrapperTestBase {

	@Test(expected=ConfigurationException.class)
	public void bootstrapNonExistentFile() {
		configure("I_DONT_EXIST");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapUnknownElement() {
		configure("invalid.xml");
	}
	
	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("minimal.xml");

		assertTrue(config.getInputs().isEmpty());
		assertTrue(config.getOutputs().isEmpty());
		assertTrue(config.getSamplers().isEmpty());
		assertTrue(config.getSharedResources().isEmpty());
		assertTrue(config.getVariables().isEmpty());
	}
}
