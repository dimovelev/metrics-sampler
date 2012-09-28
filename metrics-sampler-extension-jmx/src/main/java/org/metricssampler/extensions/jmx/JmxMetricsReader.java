package org.metricssampler.extensions.jmx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
import javax.management.remote.JMXServiceURL;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.MetaDataMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.MetricsMetaData;
import org.metricssampler.reader.OpenMetricsReaderException;

/**
 * Read metrics from a JMX server. This class is not thread safe and may not be reused in multiple samplers.
 */
public class JmxMetricsReader extends AbstractMetricsReader<JmxInputConfig> implements MetaDataMetricsReader {
	private MetricsMetaData metadata;
	private final JmxConnection connection;

	public JmxMetricsReader(final JmxInputConfig config) {
		super(config);
		try {
			this.connection = new JmxConnection(config);
		} catch (final IOException e) {
			throw new MetricReadException(e);
		}
	}

	@Override
	protected void defineCustomVariables(final Map<String, Object> variables) {
		try {
			final JMXServiceURL url = new JMXServiceURL(config.getUrl());
			final String host = url.getHost();
			if (host != null) {
				variables.put("input.host", host);
				try {
					final InetAddress inetAddress = InetAddress.getByName(host);
					final String hostname = inetAddress.getHostName();
					variables.put("input.fqhn", hostname);
					final int dotIdx = hostname.indexOf('.');
					if (dotIdx > 0) {
						variables.put("input.hostname", hostname.substring(0, dotIdx));
					} else {
						variables.put("input.hostname", hostname);
					}
					variables.put("input.ip", inetAddress.getHostAddress());
				} catch (final UnknownHostException e) {
					// ignore
				}
			}
		} catch (final MalformedURLException e) {
			e.printStackTrace();
			// ignore
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
		final long start = System.currentTimeMillis();
		logger.debug("Loading metadata from "+config.getUrl());
		final MBeanServerConnection serverConnection = connection.getServerConnection();
		final List<MetricName> result = new LinkedList<MetricName>();
		try {
			final Set<ObjectName> objectNames = serverConnection.queryNames(null, null);
			for (final ObjectName objectName : objectNames) {
				if (isIgnored(objectName)) {
					logger.debug("Ignoring " + objectName.getCanonicalName());
					continue;
				}
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
	                			logger.debug("Could not get composite type for attribute {} of {}", attribute, objectName.getCanonicalName());
	                		}
	                	} else {
		                	result.add(new JmxMetricName(objectName, attribute.getName(), null, attribute.getDescription()));
	                	}
	                }
	            } catch (final Exception e) {
	            	logger.warn("Failed to read metadata of JMX bean with name \"" + objectName.getCanonicalName() + "\". Skipping.", e);
	            }
			}
		} catch (final IOException e) {
			throw new MetricReadException("Failed to establish connection", e);
		}
		logger.debug("Loaded "+result.size()+" attributes");
		final long end = System.currentTimeMillis();
		timingsLogger.debug("Discovered {} metrics in {} ms", result.size(), end - start);
		return result;
	}

	protected boolean isIgnored(final ObjectName objectName) {
		final String canonicalName = objectName.getCanonicalName();
		for (final Pattern pattern : config.getIgnoredObjectNames()) {
			if (pattern.matcher(canonicalName).matches()) {
				return true;
			}
		}
		return false;
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
			final long start = System.currentTimeMillis();
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
			final long end = System.currentTimeMillis();
			timingsLogger.debug("Read metric {} in {} ms", metric.getName(), end-start);
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
				logger.info("Connecting to JMX server");
				connection.connect();
			} catch (final IOException e) {
				throw new OpenMetricsReaderException(e);
			}
			metadata = new MetricsMetaData(readMetaData());
		}
	}

	@Override
	public void close() throws MetricReadException {
		if (connection.isEstablished() && !config.isPersistentConnection()) {
			connection.disconnect();
			metadata = null;
		}
	}

	@Override
	public Iterable<MetricName> readNames() {
		return metadata;
	}
}
