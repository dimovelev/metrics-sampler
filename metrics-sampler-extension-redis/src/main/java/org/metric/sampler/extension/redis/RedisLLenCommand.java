package org.metric.sampler.extension.redis;

public class RedisLLenCommand extends RedisCommand {
	private final String key;

	public RedisLLenCommand(final int database, final String key) {
		super(database);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
