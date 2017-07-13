package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.extensions.graphite.GraphiteOutputConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BootstrapperGraphiteOutputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapGraphiteOutput() {
		final Configuration config = configure("graphite/complete.xml");
		
		final GraphiteOutputConfig output = assertSingleOutput(config, GraphiteOutputConfig.class);
		assertTrue(output.isDefault());
		assertEquals("graphite", output.getName());
		assertEquals("localhost", output.getHost());
		assertEquals(2003, output.getPort());
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingName() {
		configure("graphite/missing-name.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingHost() {
		configure("graphite/missing-host.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapGraphiteOutputMissingPort() {
		configure("graphite/missing-port.xml");
	}

}
