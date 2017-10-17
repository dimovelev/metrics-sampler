package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.jmx.JmxInputConfig;

import static org.junit.Assert.*;

public class BootstrapperJmxInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("jmx/complete.xml");
		
		final JmxInputConfig item = assertSingleInput(config, JmxInputConfig.class);
		assertComplete(item);
	}

	private void assertComplete(final JmxInputConfig item) {
		assertEquals("jmx", item.getName());
		assertEquals("username", item.getUsername());
		assertEquals("password", item.getPassword());
		assertEquals("url", item.getUrl());
		assertEquals("provider.packages", item.getProviderPackages());
		assertFalse(item.isPersistentConnection());
		assertSocketOptions(item.getSocketOptions(), 5, 10, 16384, 32768);
		assertEquals(1, item.getConnectionProperties().size());
		assertEquals("value", item.getConnectionProperties().get("key"));
		assertEquals(1, item.getIgnoredObjectNames().size());
		assertEquals("ignored_.+", item.getIgnoredObjectNames().iterator().next().toString());
		assertSingleStringVariable(item.getVariables(), "string", "value");
	}
	
	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("jmx/template.xml");
		
		final JmxInputConfig item = assertInput(config, "jmx", JmxInputConfig.class);
		assertComplete(item);
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("jmx/minimal.xml");
		
		final JmxInputConfig item = assertSingleInput(config, JmxInputConfig.class);
		assertEquals("jmx", item.getName());
		assertEquals("username", item.getUsername());
		assertEquals("password", item.getPassword());
		assertEquals("url", item.getUrl());
		assertEquals("provider.packages", item.getProviderPackages());
		assertTrue(item.isPersistentConnection());
		assertNull(item.getSocketOptions());
		assertTrue(item.getConnectionProperties().isEmpty());
		assertTrue(item.getIgnoredObjectNames().isEmpty());
	}
}
