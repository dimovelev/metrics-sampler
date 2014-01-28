package org.metric.sampler.extension.redis;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notNegative;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class RedisCommandXBean {
	@XStreamAsAttribute
	private Integer database;

	public Integer getDatabase() {
		return database;
	}

	public void setDatabase(final Integer database) {
		this.database = database;
	}

	protected int getDatabaseInt() {
		return database == null ? 0 : database;
	}

	public void validate() {
		notNegative(this, "database", getDatabase());
	}

	public abstract RedisCommand toConfig();
}
