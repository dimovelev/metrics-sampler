package org.metricssampler.reader;

import org.apache.commons.beanutils.BeanUtils;
import org.metricssampler.config.InputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public abstract class AbstractMetricsReader<T extends InputConfig> implements MetricsReader {
	protected static final String CONFIG_VAR_PREFIX = "input";
	protected static final Set<String> IGNORED_CONFIG_PROPERTIES = new HashSet<>(Arrays.asList("class", "variables"));
	protected final T config;
	protected final Logger logger;
	protected final Logger timingsLogger;
	protected final Map<String, Object> variables;

	protected AbstractMetricsReader(final T config) {
		this.config = config;
		this.logger = LoggerFactory.getLogger("reader." + config.getName());
		this.timingsLogger = LoggerFactory.getLogger("timings.reader");
		variables = prepareVariables();
	}

	private Map<String, Object> prepareVariables() {
		final Map<String, Object> result = new HashMap<>();
		result.putAll(config.getVariables());
		try {
			@SuppressWarnings("unchecked")
			final Map<String, Object> configProperties = BeanUtils.describe(config);

			for (final Entry<String, Object> entry : configProperties.entrySet()) {
				final String name = entry.getKey();
				if (!IGNORED_CONFIG_PROPERTIES.contains(name)) {
					result.put(CONFIG_VAR_PREFIX + "." + name, entry.getValue());
				}
			}
		} catch (final IllegalAccessException e) {
			logger.warn("Failed to introspect configuration bean: " + config, e);
		} catch (final InvocationTargetException e) {
			logger.warn("Failed to introspect configuration bean: " + config, e);
		} catch (final NoSuchMethodException e) {
			logger.warn("Failed to introspect configuration bean: " + config, e);
		}
		defineCustomVariables(result);
		return Collections.unmodifiableMap(result);
	}

	protected void defineCustomVariables(final Map<String, Object> variables) {
		// no custom variables by default
	}

	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + config.getName() + "]";
	}

	@Override
	public void reset() {
		// do nothing in this case
	}
	
	
}
