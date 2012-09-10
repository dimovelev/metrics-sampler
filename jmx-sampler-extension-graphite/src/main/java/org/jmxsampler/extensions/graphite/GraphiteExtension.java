package org.jmxsampler.extensions.graphite;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.service.Extension;
import org.jmxsampler.service.LocalObjectFactory;

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

}
