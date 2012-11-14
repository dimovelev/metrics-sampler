package org.metric.sampler.extension.redis;

public class RedisHLenCommand extends RedisCommand {
	private final String key;

	public RedisHLenCommand(final int database, final String key) {
		super(database);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
