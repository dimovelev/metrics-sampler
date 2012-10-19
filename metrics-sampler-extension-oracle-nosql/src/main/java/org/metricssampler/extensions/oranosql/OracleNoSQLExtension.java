package org.metricssampler.extensions.oranosql;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class OracleNoSQLExtension implements Extension {

	@Override
	public String getName() {
		return "oracle-nosql";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(OracleNoSQLInputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new OracleNoSQLObjectFactory();
	}

	@Override
	public void initialize() {
		// do not need to do anything
	}

}
