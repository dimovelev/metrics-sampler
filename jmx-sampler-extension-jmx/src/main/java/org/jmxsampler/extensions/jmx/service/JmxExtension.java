package org.jmxsampler.extensions.jmx.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.extensions.jmx.reader.IgnoreObjectNameXBean;
import org.jmxsampler.extensions.jmx.reader.JmxReaderXBean;
import org.jmxsampler.service.Extension;
import org.jmxsampler.service.LocalObjectFactory;

public class JmxExtension implements Extension {

	@Override
	public String getName() {
		return "jmx";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JmxReaderXBean.class);
		result.add(IgnoreObjectNameXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new JmxObjectFactory();
	}

}
