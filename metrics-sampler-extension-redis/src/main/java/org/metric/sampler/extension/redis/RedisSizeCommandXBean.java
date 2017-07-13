package org.metric.sampler.extension.redis;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.HashSet;
import java.util.Set;

@XStreamAlias("redis-size")
public class RedisSizeCommandXBean extends RedisCommandXBean {
	@XStreamAsAttribute
	private String keys;

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	@Override
	public RedisCommand toConfig() {
		Set<String> actualKeys = new HashSet<>();
		for (String key : keys.split(",")) {
			actualKeys.add(key.trim());
		}
		return new RedisSizeCommand(getDatabaseInt(), actualKeys);
	}
}
