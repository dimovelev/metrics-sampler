package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.apachestatus.ApacheStatusInputConfig;

import static org.junit.Assert.*;

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
		assertNotNull(item.getSocketOptions());
		assertEquals(5, item.getSocketOptions().getConnectTimeout());
		assertEquals(10, item.getSocketOptions().getSoTimeout());
		assertTrue(item.getSocketOptions().isKeepAlive());
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
