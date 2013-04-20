package org.metric.sampler.extension.redis;

public class RedisSLenCommand extends RedisCommand {
	private final String key;

	public RedisSLenCommand(final int database, final String key) {
		super(database);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
