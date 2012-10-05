package org.metric.sampler.extension.redis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class RedisExtension implements Extension {

	@Override
	public String getName() {
		return "redis";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(RedisInputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new RedisObjectFactory();
	}

	@Override
	public void initialize() {
		// do not need to do anything
	}

}
