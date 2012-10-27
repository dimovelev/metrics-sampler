package org.metricssampler.extensions.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.resources.SharedResource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

public class JdbcConnectionPool implements SharedResource {
	private final PooledDataSource datasource;

	public JdbcConnectionPool(final JdbcConnectionPoolConfig config) {
		datasource = createDataSource(config);
	}

	protected ComboPooledDataSource createDataSource(final JdbcConnectionPoolConfig config) {
		final ComboPooledDataSource result = new ComboPooledDataSource();
		if (!config.getOptions().isEmpty()) {
			final Properties props = new Properties();
			props.putAll(config.getOptions());
			result.setProperties(props);
		}
		result.setDataSourceName(config.getName());
		try {
			result.setDriverClass(config.getDriver());
		} catch (final PropertyVetoException e) {
			throw new ConfigurationException("Failed to set driver", e);
		}
		result.setUser(config.getUsername());
		result.setPassword(config.getPassword());
		result.setJdbcUrl(config.getUrl());
		result.setMinPoolSize(config.getMinSize());
		result.setMaxPoolSize(config.getMaxSize());
		return result;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}
	
}
