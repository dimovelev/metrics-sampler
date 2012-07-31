package org.jmxsampler.extensions.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jmxsampler.reader.AbstractMetricsReader;
import org.jmxsampler.reader.MetricReadException;
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
		final Properties props = new Properties();
		props.put("user", config.getUsername());
		props.put("password", config.getPassword());
		try {
			connection = DriverManager.getConnection(config.getUrl().toString(), props);
		} catch (final SQLException e) {
			throw new MetricReadException("Failed to connect to DB", e);
		}
	}

	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (final SQLException e) {
				logger.warn("Will ignore exception thrown during connection closing", e);
			}
		}
		connection = null;
	}

	protected void assertConnected() {
		if (connection != null) {
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
	public Object readMetric(final SourceMetricMetaData metric) throws MetricReadException {
		return null;
	}

}
