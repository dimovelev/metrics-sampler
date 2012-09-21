package org.metricssampler.extensions.jmx;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class JmxExtension implements Extension {

	@Override
	public String getName() {
		return "jmx";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JmxInputXBean.class);
		result.add(IgnoreObjectNameXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new JmxObjectFactory();
	}

	@Override
	public void initialize() {
		// do not need to do anything
	}

}
