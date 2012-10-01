package org.metricssampler.extensions.apachestatus;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class ApacheStatusExtension implements Extension {
	@Override
	public String getName() {
		return "apache-status";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ApacheStatusInputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new ApacheStatusObjectFactory();
	}

	@Override
	public void initialize() {
	}

}
