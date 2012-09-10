package org.metricssampler.extensions.jdbc;

import java.sql.DriverManager;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.service.Extension;
import org.metricssampler.service.LocalObjectFactory;

public class JdbcExtension implements Extension {
	public JdbcExtension() {
		/**
		 * WTF: load the drivers in the caller thread
		 */
		DriverManager.getDrivers();
	}
	
	@Override
	public String getName() {
		return "jdbc";
	}

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JdbcInputXBean.class);
		return result;
	}

	@Override
	public LocalObjectFactory getObjectFactory() {
		return new JdbcObjectFactory();
	}

}
