package org.jmxsampler.extensions.base.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.extensions.base.sampler.DefaultSamplerXBean;
import org.jmxsampler.extensions.base.transformer.regexp.RegExpMappingXBean;
import org.jmxsampler.extensions.base.writer.console.ConsoleWriterXBean;
import org.jmxsampler.service.Extension;
import org.jmxsampler.service.LocalObjectFactory;

public class BaseExtension implements Extension {

	@Override
	public String getName() {
		return "base";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ConsoleWriterXBean.class);
		result.add(RegExpMappingXBean.class);
		result.add(DefaultSamplerXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new BaseObjectFactory();
	}

}
