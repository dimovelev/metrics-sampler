package org.metricssampler.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.metricssampler.config.InputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMetricsReader<T extends InputConfig> implements MetricsReader {
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
		final Map<String, Object> result = new HashMap<String, Object>();
		result.putAll(config.getVariables());
		result.put("input.name", config.getName());
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
}
