package org.jmxsampler.extensions.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.reader.AbstractMetricsReader;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.SourceMetricMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcMetricsReader extends AbstractMetricsReader {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JdbcReaderConfig config;
	private Connection connection;
	 
	public JdbcMetricsReader(final JdbcReaderConfig config) {
		this.config = config;
	}

	@Override
	public void open() throws MetricReadException {
		connect();
	}

	protected void connect() {
		loadJdbcDriver();
		final Properties props = new Properties();
		props.put("user", config.getUsername());
		props.put("password", config.getPassword());
		try {
			logger.debug("Connecting to {} as {}", config.getUrl(), config.getUsername());
			connection = DriverManager.getConnection(config.getUrl(), props);
		} catch (final SQLException e) {
			throw new MetricReadException("Failed to connect to DB", e);
		}
	}

	protected void loadJdbcDriver() {
		// WTF: with multiple threads the automatic driver detection did not work that is why we load the driver explicitly
		try {
			Class.forName(config.getDriver());
		} catch (final ClassNotFoundException e) {
			throw new MetricReadException("Failed to load jdbc driver \"" + config.getDriver() + "\": "+e.getMessage(), e);
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
	public Collection<SourceMetricMetaData> getMetaData() throws MetricReadException {
		return null;
	}

	@Override
	public Map<String, String> getTransformationContext() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("reader.name", config.getName());
		return result;
	}

	@Override
	public Map<SourceMetricMetaData, MetricValue> readAllMetrics() throws MetricReadException {
		assertConnected();
		final Map<SourceMetricMetaData, MetricValue> result = new HashMap<SourceMetricMetaData, MetricValue>();
		for (final String query : config.getQueries()) {
			readMetricsFromQuery(query, result);
		}
		return result;
	}

	protected void readMetricsFromQuery(final String query, final Map<SourceMetricMetaData, MetricValue> result) {
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
					final SourceMetricMetaData metric = new SourceMetricMetaData(key, resultSet.getMetaData().getColumnName(1));
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
	
	
}
