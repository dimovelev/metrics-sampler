package org.metricssampler.extensions.jdbc;

import java.sql.DriverManager;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

public class JdbcExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JdbcInputXBean.class);
		return result;
	}

	@Override
	public void initialize() {
		/**
		 * WTF: load the drivers in the caller thread
		 */
		DriverManager.getDrivers();
	}

	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof JdbcInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new JdbcMetricsReader((JdbcInputConfig) config);
	}
}
