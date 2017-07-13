package org.metricssampler.extensions.jmx;

import org.metricssampler.reader.*;
import org.metricssampler.util.VariableUtils;

import javax.management.*;
import javax.management.openmbean.*;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Read metrics from a JMX server. This class is not thread safe and may not be reused in multiple samplers.
 */
public class JmxMetricsReader extends AbstractMetricsReader<JmxInputConfig> implements MetaDataMetricsReader {
	private MetricsMetaData metadata;
	private final JmxConnection connection;

	/**
	 * Cache for data read from the JMX server in the current session (time between open() and close()). This is necessary because we expose
	 * more metric names than actually available JMX metrics (we introspect each value and generate multiple metric names for each property
	 * and property of the property (recursively)). If we do not cache the values we would read the same data from JMX multiple times (which
	 * probably also has minor performance impact) to return different properties of the same data which could make the results inconsistent
	 * (one property read from one instance and another property from another instance).
	 */
	private final Map<JmxMetricId, Object> values = new HashMap<>();

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
			VariableUtils.addHostVariables(variables, "input", url.getHost());
		} catch (final MalformedURLException e) {
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
		logger.debug("Loading metadata from " + config.getUrl());
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
					for (final MBeanAttributeInfo attribute : attributes) {
						introspectAttribute(serverConnection, objectName, attribute, result);
					}
				} catch (final Exception e) {
					logger.warn("Failed to read metadata of JMX bean with name \"" + objectName.getCanonicalName() + "\". Skipping.", e);
				}
			}
		} catch (final IOException e) {
			throw new MetricReadException("Failed to establish connection", e);
		}
		final int count = result.size();
		logger.info("Loaded {} attributes", count);
		final long end = System.currentTimeMillis();
		timingsLogger.debug("Discovered {} metrics in {} ms", count, end - start);
		return result;
	}

	protected void introspectAttribute(final MBeanServerConnection serverConnection, final ObjectName objectName,
			final MBeanAttributeInfo attribute, final List<MetricName> result) throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		final Descriptor descriptor = attribute.getDescriptor();
		final Object openTypeField = descriptor.getFieldValue("openType");
		if (openTypeField != null) {
			// open type attribute => try to introspect the data
			final OpenType<?> openType = (OpenType<?>) openTypeField;
			final Boolean enabled = (Boolean) descriptor.getFieldValue("enabled");
			final boolean useAttribute = enabled == null || enabled;
			if (useAttribute) {
				if (openType instanceof SimpleType) {
					// no need to introspect simple types
					result.add(new JmxMetricName(objectName, attribute.getName(), PropertyPath.empty(), attribute.getDescription()));
				} else {
					// lets introspect the open type value
					final Object value = serverConnection.getAttribute(objectName, attribute.getName());
					final Set<PropertyPath> properties = new HashSet<>();
					introspectAttributeWithValue(PropertyPath.empty(), value, openType, properties);
					for (final PropertyPath path : properties) {
						result.add(new JmxMetricName(objectName, attribute.getName(), path, null));
					}
				}
			}
		} else {
			// not an open type => just add it as it is
			result.add(new JmxMetricName(objectName, attribute.getName(), PropertyPath.empty(), null));
		}
	}

	private void introspectAttributeWithValue(final PropertyPath propertyPath, final Object value, final OpenType<?> openType,
			final Set<PropertyPath> result) {
		if (openType instanceof SimpleType) {
			// this can only happen when we are called recursively. for the first call we have already made sure this is not a simple type
			result.add(propertyPath);
		} else if (openType instanceof CompositeType) {
			processCompositeProperty(propertyPath, value, (CompositeType) openType, result);
		} else if (openType instanceof TabularType) {
			processTableProperty(propertyPath, value, (TabularType) openType, result);
		} else if (openType instanceof ArrayType) {
			// we do not support arrays (yet)
			logger.debug("Property path {} denotes an array which is not supported (yet): {}", propertyPath, openType);
		} else {
			logger.warn("Unsupported open type {} for property path {}", openType, propertyPath);
		}
	}

	/**
	 * Some of the columns are used as an index to uniquely identify a row. We use their values as property path in the form [value-1,
	 * value-2]. The cells of the table can be again complex types so we need to go recursively into them
     * @param propertyPath path so far
     * @param value the value containing tabular data
     * @param type the type description
     * @param result a place to store the discovered new properties
	 */
	protected void processTableProperty(final PropertyPath propertyPath, final Object value, final TabularType type,
			final Set<PropertyPath> result) {
		if (value instanceof TabularData) {
			final TabularData tabularValue = (TabularData) value;
			final Set<String> valueColumns = new HashSet<>();
			valueColumns.addAll(type.getRowType().keySet());
			valueColumns.removeAll(type.getIndexNames());
			for (final Object untypedRow : tabularValue.values()) {
				final CompositeData row = (CompositeData) untypedRow;
				final RowPathSegment segment = RowPathSegment.fromRow(type, row);
				final PropertyPath childPropertyPath = propertyPath.add(segment);
				for (final String column : valueColumns) {
					final Object cellValue = row.get(column);
					final OpenType<?> cellType = row.getCompositeType().getType(column);
					introspectAttributeWithValue(childPropertyPath.add(new PropertyPathSegment(column)), cellValue, cellType, result);
				}
			}
		} else {
			if (value != null) {
				logger.warn("Unsupported value {} for property path {}", value, propertyPath);
			}
		}
	}

	/**
	 * Process a composite property. Its value can in turn be of composite type so we need to do this recursively
     * @param propertyPath path so far
     * @param value the value containing composite data
     * @param type the type description
     * @param result a place to store the discovered new properties
	 */
	protected void processCompositeProperty(final PropertyPath propertyPath, final Object value, final CompositeType type,
			final Set<PropertyPath> result) {
		for (final String propertyName : type.keySet()) {
			final PropertyPath childPropertyPath = propertyPath.add(new PropertyPathSegment(propertyName));
			final Object propertyValue = value instanceof CompositeData ? ((CompositeData) value).get(propertyName) : value;
			introspectAttributeWithValue(childPropertyPath, propertyValue, type.getType(propertyName), result);
		}
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
		logger.debug("Reading " + metric.getName());
		final JmxMetricId jmxId = actualMetric.getJmxId();
		Object value = values.get(jmxId);
		if (value != null) {
			logger.debug("Value already read before. Reusing it.");
		} else {
			final MBeanServerConnection serverConnection = connection.getServerConnection();
			try {
				final long start = System.currentTimeMillis();
				value = serverConnection.getAttribute(actualMetric.getObjectName(), actualMetric.getAttributeName());
				values.put(new JmxMetricId(actualMetric.getObjectName(), actualMetric.getAttributeName()), value);
				final long end = System.currentTimeMillis();
				timingsLogger.debug("Read metric {} in {} ms", metric.getName(), end - start);
			} catch (final AttributeNotFoundException e) {
				throw new MetricReadException(e);
			} catch (final InstanceNotFoundException e) {
				throw new MetricReadException(e);
			} catch (final MBeanException e) {
				throw new MetricReadException(e);
			} catch (final RuntimeMBeanException e) {
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
		if (value != null) {
			final Object result = evaluatePath(actualMetric.getProperty(), value);
			return new MetricValue(System.currentTimeMillis(), result);
		} else {
			return new MetricValue(System.currentTimeMillis(), null);
		}
	}

	protected Object evaluatePath(final PropertyPath path, final Object value) {
		Object result = value;
		for (final PathSegment segment : path.getSegments()) {
			result = segment.getValue(result);
			if (result == null) {
				return null;
			}
		}
		return result;
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
		values.clear();
	}

	@Override
	public void close() throws MetricReadException {
		if (!config.isPersistentConnection()) {
			forceDisconnect();
		}
		values.clear();
	}

	private void forceDisconnect() {
		if (connection.isEstablished()) {
			logger.info("Disconnecting");
			connection.disconnect();
			metadata = null;
			values.clear();
		}
	}

	@Override
	public Iterable<MetricName> readNames() {
		return metadata;
	}

	@Override
	public void reset() {
		forceDisconnect();
	}
}
