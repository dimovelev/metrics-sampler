package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.extensions.base.ConsoleOutputConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BootstrapperConsoleOutputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapConsoleOutput() {
		final Configuration config = configure("console/complete.xml");
		
		final ConsoleOutputConfig output = assertSingleOutput(config, ConsoleOutputConfig.class);
		assertTrue(output.isDefault());
		assertEquals("console", output.getName());
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapConsoleOutputMissingName() {
		configure("console/missing-name.xml");
	}

}
