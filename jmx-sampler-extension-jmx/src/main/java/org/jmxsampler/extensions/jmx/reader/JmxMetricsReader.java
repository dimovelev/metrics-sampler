package org.jmxsampler.extensions.jmx.reader;

import java.io.IOException;
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
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;

import org.jmxsampler.reader.MetaDataMetricsReader;
import org.jmxsampler.reader.MetricName;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.MetricValue;
import org.jmxsampler.reader.MetricsMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read metrics from a JMX server. This class is not thread safe and may not be reused in multiple samplers.
 */
public class JmxMetricsReader implements MetaDataMetricsReader {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JmxReaderConfig config;
	private MetricsMetaData metadata;
	private final Map<String, String> context;
	private final JmxConnection connection;

	public JmxMetricsReader(final JmxReaderConfig config) {
		this.config = config;
		context = prepareContext();
		try {
			this.connection = new JmxConnection(config);
		} catch (final IOException e) {
			throw new MetricReadException(e);
		}
	}

	@Override
	public MetricsMetaData getMetaData() {
		assertConnected();
		return metadata;
	}

	protected void assertConnected() {
		if (!connection.isEstablished()) {
			throw new IllegalStateException("You are not connected. Please call open() first.");
		}
	}

	protected List<MetricName> readMetaData() {
		logger.debug("Loading metadata from "+config.getUrl());
		final MBeanServerConnection serverConnection = connection.getServerConnection();
		final List<MetricName> result = new LinkedList<MetricName>();
		try {
			final Set<ObjectName> objectNames = serverConnection.queryNames(null, null);
			for (final ObjectName objectName : objectNames) {
				try {
	                final MBeanInfo info = serverConnection.getMBeanInfo(objectName);
	                final MBeanAttributeInfo[] attributes = info.getAttributes();
	                for(final MBeanAttributeInfo attribute : attributes) {
	                	if ("javax.management.openmbean.CompositeData".equals(attribute.getType())) {
	                		final CompositeType compositeType = getCompositeTypeForAttribute(serverConnection, objectName, attribute);
	                		if (compositeType != null) {
		                		for (final String key : compositeType.keySet()) {
				                	result.add(new JmxMetricName(objectName, attribute.getName(), key, compositeType.getDescription(key)));
		                		}
	                		} else {
	                			logger.debug("Could not get composite type for attribute {} of {}", attribute, objectName);
	                		}
	                	} else {
		                	result.add(new JmxMetricName(objectName, attribute.getName(), null, attribute.getDescription()));
	                	}
	                }
	            } catch (final Exception e) {
	            	logger.warn("Failed to read metadata of JMX bean with name \"" + objectName + "\"", e);
	            }
			}
		} catch (final IOException e) {
			throw new MetricReadException("Failed to establish connection", e);
		}
		logger.debug("Loaded "+result.size()+" attributes");
		return result;
	}

	protected CompositeType getCompositeTypeForAttribute(final MBeanServerConnection serverConnection, final ObjectName objectName,
			final MBeanAttributeInfo attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException,
			ReflectionException, IOException {
		final CompositeType result = (CompositeType) attribute.getDescriptor().getFieldValue("openType");
		if (result != null) {
			return result;
		}
		final CompositeData data = (CompositeData) serverConnection.getAttribute(objectName, attribute.getName());
		if (data != null) {
			return data.getCompositeType();
		}
		return null;
	}

	@Override
	public MetricValue readMetric(final MetricName metric) {
		final JmxMetricName actualMetric = (JmxMetricName) metric;
		logger.debug("Reading "+metric.getName());
		final MBeanServerConnection serverConnection = connection.getServerConnection();
		try {
			final Object value = serverConnection.getAttribute(actualMetric.getObjectName(), actualMetric.getAttributeName());
			if (actualMetric.isComposite()) {
				if (value instanceof CompositeDataSupport) {
					final CompositeDataSupport compositeData = (CompositeDataSupport) value;
					final Object compositeValue = compositeData.get(actualMetric.getKey());
					return new MetricValue(System.currentTimeMillis(), compositeValue);
				} else {
					logger.warn("Expected a composite value for \"" + actualMetric.getName() + "\" but got "+value);
				}
			}
			return new MetricValue(System.currentTimeMillis(), value);
		} catch (final AttributeNotFoundException e) {
			throw new MetricReadException(e);
		} catch (final InstanceNotFoundException e) {
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
			metadata = new MetricsMetaData(readMetaData());
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

	@Override
	public Iterable<MetricName> readNames() {
		return metadata;
	}
}
