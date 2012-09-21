package org.metricssampler.extensions.graphite;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class GraphiteExtension implements Extension {

	@Override
	public String getName() {
		return "graphite";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(GraphiteOutputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new GraphiteObjectFactory();
	}

	@Override
	public void initialize() {
		// we do not need to do anything
	}
}
