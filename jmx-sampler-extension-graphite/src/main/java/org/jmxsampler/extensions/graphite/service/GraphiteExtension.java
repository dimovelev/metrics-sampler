package org.jmxsampler.extensions.graphite.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.extensions.graphite.writer.GraphiteWriterXBean;
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
		result.add(GraphiteWriterXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new GraphiteObjectFactory();
	}

}
