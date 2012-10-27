package org.metricssampler.extensions.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.resources.SharedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

public class JdbcConnectionPool implements SharedResource {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JdbcConnectionPoolConfig config;
	private final PooledDataSource datasource;

	public JdbcConnectionPool(final JdbcConnectionPoolConfig config) {
		this.config = config;
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

	@Override
	public void shutdown() {
		try {
			logger.info("Shutting down JDBC connection pool {}", config.getName());
			datasource.close();
			logger.info("JDBC connection pool {} was shutdown", config.getName());
		} catch (final SQLException e) {
			logger.warn("Failed to close JDBC connection pool " + config.getName(), e);
		}
	}
}
