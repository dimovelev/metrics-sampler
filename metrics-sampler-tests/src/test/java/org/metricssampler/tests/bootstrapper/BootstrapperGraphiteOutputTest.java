package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.extensions.graphite.GraphiteOutputConfig;
import org.metricssampler.service.Bootstrapper;

public class BootstrapperGraphiteOutputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapGraphiteOutput() {
		final Bootstrapper result = bootstrap("graphite/complete.xml");
		
		final Configuration config = result.getConfiguration();
		assertNotNull(config);
		final GraphiteOutputConfig output = assertSingleOutput(config, GraphiteOutputConfig.class);
		assertTrue(output.isDefault());
		assertEquals("graphite", output.getName());
		assertEquals("localhost", output.getHost());
		assertEquals(2003, output.getPort());
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingName() {
		bootstrap("graphite/missing-name.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingHost() {
		bootstrap("graphite/missing-host.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingPort() {
		bootstrap("graphite/missing-port.xml");
	}

}
