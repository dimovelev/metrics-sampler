package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.extensions.base.ConsoleOutputConfig;
import org.metricssampler.service.Bootstrapper;

public class BootstrapperConsoleOutputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapConsoleOutput() {
		final Bootstrapper result = bootstrap("console/complete.xml");
		
		final Configuration config = result.getConfiguration();
		assertNotNull(config);
		final ConsoleOutputConfig output = assertSingleOutput(config, ConsoleOutputConfig.class);
		assertTrue(output.isDefault());
		assertEquals("console", output.getName());
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapConsoleOutputMissingName() {
		bootstrap("console/missing-name.xml");
	}

}
