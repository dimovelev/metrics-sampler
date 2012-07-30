package org.jmxsampler.extensions.modqos;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.service.Extension;
import org.jmxsampler.service.LocalObjectFactory;

public class ModQosExtension implements Extension {

	@Override
	public String getName() {
		return "mod_qos";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ModQosReaderXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new ModQosObjectFactory();
	}

}
