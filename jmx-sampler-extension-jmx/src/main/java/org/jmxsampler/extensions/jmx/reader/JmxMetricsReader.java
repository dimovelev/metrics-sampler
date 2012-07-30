package org.jmxsampler.extensions.jmx.reader;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.jmxsampler.reader.AbstractMetricsReader;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.SourceMetricMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read metrics from a JMX server. This class is not thread safe and may not be reused in multiple samplers.
 */
public class JmxMetricsReader extends AbstractMetricsReader {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JmxReaderConfig config;
	private List<SourceMetricMetaData> metadata;
	private Map<String, String> context;
	private final JmxConnection connection;

	public JmxMetricsReader(final JmxReaderConfig config) {
		this.config = config;
		try {
			this.connection = new JmxConnection(config);
		} catch (final IOException e) {
			throw new MetricReadException(e);
		}
	}

	@Override
	public Collection<SourceMetricMetaData> getMetaData() {
		assertConnected();
		return Collections.unmodifiableList(metadata);
	}

	protected void assertConnected() {
		if (!connection.isEstablished()) {
			throw new IllegalStateException("You are not connected. Please call open() first.");
		}
	}

	protected List<SourceMetricMetaData> readMetaData() {
		logger.debug("Loading metadata from "+config.getUrl());
		final MBeanServerConnection serverConnection = connection.getServerConnection();
		final List<SourceMetricMetaData> result = new LinkedList<SourceMetricMetaData>();
		try {
			final Set<ObjectName> objectNames = serverConnection.queryNames(null, null);
			for (final ObjectName objectName : objectNames) {
				final String name = objectName.toString();
				try {
	                final MBeanInfo info = serverConnection.getMBeanInfo(objectName);
	                final MBeanAttributeInfo[] attributes = info.getAttributes();
	                for(final MBeanAttributeInfo attribute : attributes) {
	                	final SourceMetricMetaData metadata = new SourceMetricMetaData(name+"."+attribute.getName(), attribute.getDescription());
	                	result.add(metadata);
	                }
	            } catch (final Exception e) {
	            	logger.warn("Failed to read metadata of JMX bean with name \"" + name+"\"");
	            }
			}
		} catch (final IOException e) {
			throw new MetricReadException("Failed to establish connection", e);
		}
		logger.debug("Loaded "+result.size()+" attributes");
		return result;
	}

	@Override
	public Object readMetric(final SourceMetricMetaData metric) {
		final String name = metric.getName();
		logger.debug("Reading "+name);
		final MBeanServerConnection serverConnection = connection.getServerConnection();
		final int dotIdx = name.lastIndexOf('.');
		try {
			return serverConnection.getAttribute(new ObjectName(name.substring(0, dotIdx)), name.substring(dotIdx+1));
		} catch (final AttributeNotFoundException e) {
			throw new MetricReadException(e);
		} catch (final InstanceNotFoundException e) {
			throw new MetricReadException(e);
		} catch (final MalformedObjectNameException e) {
			throw new MetricReadException(e);
		} catch (final MBeanException e) {
			throw new MetricReadException(e);
		} catch (final ReflectionException e) {
			throw new MetricReadException(e);
		} catch (final NullPointerException e) {
			throw new MetricReadException(e);
		} catch (final IOException e) {
			reconnect();
			throw new MetricReadException(e);
		}
	}

	private void reconnect() {
		logger.info("Reconnecting");
		connection.disconnect();
		try {
			connection.connect();
		} catch (final IOException e) {
			logger.warn("Failed to reconnect", e);
			connection.disconnect();
		}
	}

	@Override
	public void open() {
		if (!connection.isEstablished()) {
			try {
				connection.connect();
			} catch (final IOException e) {
				throw new MetricReadException("Failed to connect", e);
			}
			metadata = readMetaData();
			context = prepareContext();
			notifyOnConnected();
		}
	}

	private Map<String, String> prepareContext() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("reader.name", config.getName());
		return result;
	}

	@Override
	public void close() throws MetricReadException {
		if (connection.isEstablished() && !config.isPersistentConnection()) {
			connection.disconnect();
			metadata = null;
		}
	}

	@Override
	public Map<String, String> getTransformationContext() {
		return context;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + config.getName() + "]";
	}


}
