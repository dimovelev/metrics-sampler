package org.metricssampler.extensions.jdbc;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.reader.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.metricssampler.util.CloseableUtils.closeQuietly;

public class JdbcMetricsReader extends AbstractMetricsReader<JdbcInputConfig> implements BulkMetricsReader {
	private final JdbcConnectionPool connectionPool;
	private Connection connection;

	public JdbcMetricsReader(final JdbcInputConfig config, final JdbcConnectionPool connectionPool) {
		super(config);
		this.connectionPool = connectionPool;
	}

	@Override
	public void open() throws MetricReadException {
		try {
			logger.debug("Fetching connection from pool {}", config.getPool());
			this.connection = connectionPool.getConnection();
		} catch (final SQLException e) {
			throw new OpenMetricsReaderException(e);
		}
	}

	@Override
	public void close() {
		forceDisconnect();
	}

	private void forceDisconnect() {
		if (connection != null) {
			try {
				logger.debug("Returning connection to pool {}", config.getPool());
				connection.close();
				connection = null;
			} catch (final SQLException e) {
				logger.warn("Will ignore exception thrown during connection closing", e);
			}
		} else {
			logger.debug("Not connected so nothing to do in force disconnect");
		}
	}

	@Override
	public void reset() {
		forceDisconnect();
	}

	protected void assertConnected() {
		if (connection == null) {
			throw new IllegalStateException("Not connected. Call open() first.");
		}
	}

	@Override
	public Metrics readAllMetrics() throws MetricReadException {
		assertConnected();
		final Metrics result = new Metrics();
		for (final String query : config.getQueries()) {
			readMetricsFromQuery(query, result);
		}
		return result;
	}

	protected void readMetricsFromQuery(final String query, final Metrics result) {
		logger.debug("Executing query {}", query);
		final long start = System.currentTimeMillis();
		try (final Statement statement = connection.createStatement()) {
			try (final ResultSet resultSet = statement.executeQuery(query)) {
				logger.debug("Fetching results of query {}", query);
				while (resultSet.next()) {
					final int columnCount = resultSet.getMetaData().getColumnCount();
					final String key = resultSet.getString(1);
					final String value = resultSet.getString(2);
					final SimpleMetricName metric = new SimpleMetricName(key, resultSet.getMetaData().getColumnName(1));
					if (columnCount == 2) {
						logger.debug("Using current timestamp as metric timestamp for "+key);
						result.add(metric, start, value);
					} else if (columnCount == 3) {
						logger.debug("Using timestamp from query result column 3 as metric timestamp for "+key);
						final long timestamp = resultSet.getLong(3);
						result.add(metric, timestamp, value);
					} else {
						closeQuietly(resultSet);
						throw new ConfigurationException("Query must return either 2 (name, value) or 3 columns (name, value, timestamp)");
					}
				}
			} catch (final SQLException e) {
				logger.warn("Failed to execute query \"" + query + "\"", e);
			}
			final long end = System.currentTimeMillis();
			timingsLogger.debug("Discovered {} metrics in {} ms", result.size(), end - start);
		} catch (final SQLException e) {
			reconnect();
			throw new MetricReadException("Failed to create statement. Will reconnect just in case", e);
		}
	}

	protected void reconnect() {
		close();
		open();
	}
}
