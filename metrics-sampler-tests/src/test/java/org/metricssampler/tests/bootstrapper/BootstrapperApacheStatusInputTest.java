package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.apachestatus.ApacheStatusInputConfig;

public class BootstrapperApacheStatusInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("apache-status/complete.xml");
		
		final ApacheStatusInputConfig item = assertSingleInput(config, ApacheStatusInputConfig.class);
		assertEquals("apache-status", item.getName());
		assertEquals("username", item.getUsername());
		assertEquals("password", item.getPassword());
		assertEquals("http://localhost", item.getUrl().toExternalForm());
		assertFalse(item.isPreemptiveAuthEnabled());
		assertSingleStringVariable(item.getVariables(), "string", "value");
		assertSingleEntry(item.getHeaders(), "header", "val");
	}
	
	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("apache-status/minimal.xml");

		final ApacheStatusInputConfig item = assertSingleInput(config, ApacheStatusInputConfig.class);
		assertEquals("apache-status", item.getName());
		assertNull(item.getUsername());
		assertNull(item.getPassword());
		assertEquals("http://localhost", item.getUrl().toExternalForm());
		assertTrue(item.isPreemptiveAuthEnabled());
		assertTrue(item.getVariables().isEmpty());
		assertTrue(item.getHeaders().isEmpty());
	}
}
