package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metric.sampler.extension.memcached.MemcachedInputConfig;
import org.metricssampler.config.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BootstrapperMemcachedInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("memcached/complete.xml");
		
		final MemcachedInputConfig item = assertSingleInput(config, MemcachedInputConfig.class);
		assertComplete(item);
	}

	private void assertComplete(final MemcachedInputConfig item) {
		assertEquals("memcached1", item.getName());
		assertEquals("host", item.getHost());
		assertEquals(11211, item.getPort());
		assertSocketOptions(item.getSocketOptions(), 5, 10, 16384, 32768);
		assertSingleStringVariable(item.getVariables(), "string", "value");
	}
	
	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("memcached/template.xml");
		
		final MemcachedInputConfig item = assertInput(config, "memcached1", MemcachedInputConfig.class);
		assertComplete(item);
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("memcached/minimal.xml");
		
		final MemcachedInputConfig item = assertSingleInput(config, MemcachedInputConfig.class);
		assertEquals("memcached1", item.getName());
		assertEquals("host", item.getHost());
		assertEquals(1980, item.getPort());
		assertNull(item.getSocketOptions());
	}
}
