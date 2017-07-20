package org.metricssampler.extensions.jdbc;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.AbstractExtension;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JdbcExtension extends AbstractExtension {
	public static final List<Class<?>> XBEAN_CLASSES = Arrays.asList(JdbcInputXBean.class, JdbcConnectionPoolXBean.class);

	@Override
	public Collection<Class<?>> getXBeans() {
		return XBEAN_CLASSES;
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
		final JdbcInputConfig jdbcConfig = (JdbcInputConfig) config;
		final SharedResource sharedResource = getGlobalFactory().getSharedResource(jdbcConfig.getPool());
		if (sharedResource instanceof JdbcConnectionPool) {
			return new JdbcMetricsReader(jdbcConfig, (JdbcConnectionPool) sharedResource);
		} else {
			throw new ConfigurationException(jdbcConfig.getPool() + " is not a JDBC connection pool: " + sharedResource);
		}
	}

	@Override
	public boolean supportsSharedResource(final SharedResourceConfig config) {
		return config instanceof JdbcConnectionPoolConfig;
	}

	@Override
	protected SharedResource doNewSharedResource(final SharedResourceConfig config, boolean suspended) {
		return new JdbcConnectionPool((JdbcConnectionPoolConfig) config, suspended);
	}
}
