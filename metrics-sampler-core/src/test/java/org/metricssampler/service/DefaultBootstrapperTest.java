package org.metricssampler.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DefaultBootstrapperTest {
	@Test
	public void bootstrapTiny() {
		final Bootstrapper result = DefaultBootstrapper.bootstrap("src/test/resources/config.tiny.xml", "localhost", 28123);
		assertNotNull(result.getConfiguration());
	}

	@Test
	public void bootstrapWithControlPort() {
		final Bootstrapper result = DefaultBootstrapper.bootstrap("localhost", 28123);
		assertEquals(28123, result.getControlPort());
	}

}
