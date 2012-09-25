package org.metricssampler.extensions.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.reader.SimpleMetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcMetricsReader implements BulkMetricsReader {
	private final Logger logger;
	private final JdbcInputConfig config;
	private Connection connection;
	 
	public JdbcMetricsReader(final JdbcInputConfig config) {
		this.config = config;
		this.logger = LoggerFactory.getLogger("reader."+config.getName());
	}

	@Override
	public void open() throws MetricReadException {
		connect();
	}

	protected void connect() {
		final Properties props = new Properties();
		props.putAll(config.getOptions());
		props.put("user", config.getUsername());
		props.put("password", config.getPassword());
		try {
			logger.debug("Connecting to {} as {}", config.getUrl(), config.getUsername());
			connection = DriverManager.getConnection(config.getUrl(), props);
		} catch (final SQLException e) {
			throw new OpenMetricsReaderException(e);
		}
	}

	@Override
	public void close() {
		disconnect();
	}

	protected void disconnect() {
		if (connection != null) {
			try {
				logger.debug("Disconnecting from {}", config.getUrl());
				connection.close();
			} catch (final SQLException e) {
				logger.warn("Will ignore exception thrown during connection closing", e);
			}
		}
		connection = null;
	}

	protected void assertConnected() {
		if (connection == null) {
			throw new IllegalStateException("Not connected. Call open() first.");
		}
	}

	@Override
	public Map<String, Object> getVariables() {
		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("input.name", config.getName());
		return result;
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		assertConnected();
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		for (final String query : config.getQueries()) {
			readMetricsFromQuery(query, result);
		}
		return result;
	}

	protected void readMetricsFromQuery(final String query, final Map<MetricName, MetricValue> result) {
		Statement statement = null;
		try {
			logger.debug("Executing query {}", query);
			statement = connection.createStatement();
			ResultSet resultSet = null;
			try {
				resultSet = statement.executeQuery(query);
				logger.debug("Fetching results of query {}", query);
				while (resultSet.next()) {
					final int columnCount = resultSet.getMetaData().getColumnCount();
					final String key = resultSet.getString(1);
					final String value = resultSet.getString(2);
					final SimpleMetricName metric = new SimpleMetricName(key, resultSet.getMetaData().getColumnName(1));
					if (columnCount == 2) {
						logger.debug("Using current timestamp as metric timestamp for "+key);
						result.put(metric, new MetricValue(System.currentTimeMillis(), value));
					} else if (columnCount == 3) {
						logger.debug("Using timestamp from query result column 3 as metric timestamp for "+key);
						final long timestamp = resultSet.getLong(3);
						result.put(metric, new MetricValue(timestamp, value));
					} else {
						throw new ConfigurationException("Query must return either 2 (name, value) or 3 columns (name, value, timestamp)");
					}
				}
			} catch (final SQLException e) {
				logger.warn("Failed to execute query \"" + query + "\"", e);
			} finally {
				closeQuietly(resultSet);
			}
		} catch (final SQLException e) {
			reconnect();
			throw new MetricReadException("Failed to create statement. Will reconnect just in case", e);
		} finally {
			closeQuietly(statement);
		}
	}

	protected void closeQuietly(final Statement closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final SQLException e) {
				// ignore
			}
		}
	}
	
	protected void closeQuietly(final ResultSet closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final SQLException e) {
				// ignore
			}
		}
	}

	protected void reconnect() {
		disconnect();
		connect();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+config.getName()+"]";
	}

	@Override
	public Iterable<MetricName> readNames() throws MetricReadException {
		return readAllMetrics().keySet();
	}
}
