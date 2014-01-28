package org.metric.sampler.extension.redis;

import java.util.Collections;
import java.util.Set;

public class RedisSizeCommand extends RedisCommand {
	private final Set<String> keyPatterns;
	
	public RedisSizeCommand(int database, Set<String> keyPatterns) {
		super(database);
		this.keyPatterns = Collections.unmodifiableSet(keyPatterns);
	}

	public Set<String> getKeyPatterns() {
		return keyPatterns;
	}
}
