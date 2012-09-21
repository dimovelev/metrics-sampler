package org.metricssampler.extensions.base.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.extensions.base.sampler.DefaultSamplerXBean;
import org.metricssampler.extensions.base.selector.regexp.RegExpSelectorXBean;
import org.metricssampler.extensions.base.writer.console.ConsoleOutputXBean;
import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class BaseExtension implements Extension {

	@Override
	public String getName() {
		return "base";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ConsoleOutputXBean.class);
		result.add(RegExpSelectorXBean.class);
		result.add(DefaultSamplerXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new BaseObjectFactory();
	}

	@Override
	public void initialize() {
		// we do not need to do anything
	}
}
