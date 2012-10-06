package org.metricssampler.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.metricssampler.config.ConfigurationException;

public class DefaultBootstrapperTest {
	@Test
	public void bootstrapTiny() {
		System.setProperty("control.port", "28123");
		final Bootstrapper result = DefaultBootstrapper.bootstrap("src/test/resources/config.tiny.xml");
		assertEquals(10, result.getConfiguration().getPoolSize());
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
