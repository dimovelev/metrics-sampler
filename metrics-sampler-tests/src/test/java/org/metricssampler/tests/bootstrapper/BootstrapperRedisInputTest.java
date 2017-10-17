package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metric.sampler.extension.redis.RedisInputConfig;
import org.metric.sampler.extension.redis.RedisSizeCommand;
import org.metricssampler.config.Configuration;

import static org.junit.Assert.*;

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
		assertEquals(2, item.getCommands().size());
		final RedisSizeCommand size1 = (RedisSizeCommand) item.getCommands().get(0);
		assertEquals(1, size1.getDatabase());
		assertEquals("Expected exactly one element in " + size1.getKeyPatterns(), 1, size1.getKeyPatterns().size());
		assertTrue(size1.getKeyPatterns().contains("list"));

		final RedisSizeCommand size2 = (RedisSizeCommand) item.getCommands().get(1);
		assertEquals(2, size2.getDatabase());
		assertEquals("Expected exactly two elements in " + size2.getKeyPatterns(), 2, size2.getKeyPatterns().size());
		assertTrue(size2.getKeyPatterns().contains("hash"));
		assertTrue(size2.getKeyPatterns().contains("set"));
	}

	@Test
	public void bootstrapTemplate() {
        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
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
