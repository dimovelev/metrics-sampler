package org.metricssampler.extensions.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import org.apache.commons.beanutils.BeanUtils;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.GlobalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class JdbcConnectionPool implements SharedResource {
	private static final List<String> STATS_DATASOURCE_PROPERTIES = Arrays.asList(
			"numBusyConnectionsAllUsers",
			"numConnectionsAllUsers",
			"numConnectionsAllUsers",
			"numFailedCheckinsDefaultUser",
			"numFailedCheckoutsDefaultUser",
			"numFailedIdleTestsDefaultUser",
			"numHelperThreads",
			"numIdleConnectionsAllUsers",
			"numUnclosedOrphanedConnectionsAllUsers"
	);
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JdbcConnectionPoolConfig config;
	private PooledDataSource datasource;

	public JdbcConnectionPool(final JdbcConnectionPoolConfig config) {
		this.config = config;
		startup();
		GlobalRegistry.getInstance().addSharedResource(this);
	}

	@Override
	public void startup() {
		if (datasource == null) {
			datasource = createDataSource(config);
		} else {
			logger.warn("Cannot startup as already started. Use shutdown first");
		}
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
		try {
			result.setLoginTimeout(config.getLoginTimeout());
		} catch (final SQLException e) {
			throw new ConfigurationException("Failed to set login timeout", e);
		}
		return result;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	protected void assertStarted() {
		if (datasource == null) {
			throw new IllegalStateException("I must be started to do that");
		}
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
		datasource = null;
	}

	@Override
	public Map<String, Object> getStats() {
		final String prefix = "jdbc-pools." + config.getName() + ".";
		final Map<String, Object> result = new HashMap<>();
		for (final String property : STATS_DATASOURCE_PROPERTIES) {
			try {
				final String value = BeanUtils.getProperty(datasource, property);
				result.put(prefix + property, value);
			} catch (final IllegalAccessException e) {
				result.put(property, -1);
			} catch (final InvocationTargetException e) {
				result.put(property, -1);
			} catch (final NoSuchMethodException e) {
				result.put(property, -1);
			}
		}
		return result;
	}
}
