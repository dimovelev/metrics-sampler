package org.metric.sampler.extension.redis;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("redis-slen")
public class RedisSLenCommandXBean extends RedisCommandXBean {
	@XStreamAsAttribute
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	@Override
	public RedisCommand toConfig() {
		return new RedisSLenCommand(getDatabaseInt(), key);
	}
}
