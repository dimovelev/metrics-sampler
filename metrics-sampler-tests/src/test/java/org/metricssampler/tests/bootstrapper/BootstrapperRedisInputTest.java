package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.metric.sampler.extension.redis.RedisInputConfig;
import org.metricssampler.config.Configuration;

public class BootstrapperRedisInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("redis/complete.xml");
		
		final RedisInputConfig item = assertSingleInput(config, RedisInputConfig.class);
		assertComplete(item);
	}

	private void assertComplete(final RedisInputConfig item) {
		assertEquals("redis", item.getName());
		assertEquals("host", item.getHost());
		assertEquals(2811, item.getPort());
		assertEquals("password", item.getPassword());
		assertSingleStringVariable(item.getVariables(), "string", "value");
	}

	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("redis/template.xml");
		
		final RedisInputConfig item = assertInput(config, "redis", RedisInputConfig.class);
		assertComplete(item);
	}
	
	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("redis/minimal.xml");
		
		final RedisInputConfig item = assertSingleInput(config, RedisInputConfig.class);
		assertEquals("redis", item.getName());
		assertEquals("host", item.getHost());
		assertEquals(2811, item.getPort());
		assertNull(item.getPassword());
	}
}
