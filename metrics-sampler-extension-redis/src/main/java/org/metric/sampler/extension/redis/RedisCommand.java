package org.metric.sampler.extension.redis;

public abstract class RedisCommand {
	private final int database;

	public RedisCommand(final int database) {
		this.database = database;
	}

	public int getDatabase() {
		return database;
	}
}
