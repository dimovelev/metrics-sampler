package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.metric.sampler.extension.redis.RedisHLenCommand;
import org.metric.sampler.extension.redis.RedisInputConfig;
import org.metric.sampler.extension.redis.RedisLLenCommand;
import org.metric.sampler.extension.redis.RedisSLenCommand;
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
		assertEquals(3, item.getCommands().size());
		final RedisLLenCommand llen = (RedisLLenCommand) item.getCommands().get(0);
		assertEquals(1, llen.getDatabase());
		assertEquals("list", llen.getKey());
		final RedisHLenCommand hlen = (RedisHLenCommand) item.getCommands().get(1);
		assertEquals(2, hlen.getDatabase());
		assertEquals("hash", hlen.getKey());
		final RedisSLenCommand slen = (RedisSLenCommand) item.getCommands().get(2);
		assertEquals(3, slen.getDatabase());
		assertEquals("set", slen.getKey());
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
