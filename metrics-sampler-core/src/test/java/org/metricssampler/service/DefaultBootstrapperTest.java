package org.metricssampler.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.metricssampler.config.ConfigurationException;

public class DefaultBootstrapperTest {
	@Test
	public void bootstrapTiny() {
		System.setProperty("control.port", "28123");
		final Bootstrapper result = DefaultBootstrapper.bootstrap("src/test/resources/config.tiny.xml");
		assertNotNull(result.getConfiguration());
	}

	@Test(expected=ConfigurationException.class)
	public void bootstrapWithoutControlPort() {
		System.clearProperty("control.port");
		DefaultBootstrapper.bootstrap();
	}
	
	@Test
	public void bootstrapWithControlPort() {
		System.setProperty("control.port", "28123");

		final Bootstrapper result = DefaultBootstrapper.bootstrap();
		
		System.clearProperty("control.port");
		assertEquals(28123, result.getControlPort());
	}

}
