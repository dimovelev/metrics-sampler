package org.metricssampler.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
