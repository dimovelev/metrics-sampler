package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for input XBeans.
 */
public abstract class InputXBean extends TemplatableXBean {
	private List<VariableXBean> variables;
	
	public List<VariableXBean> getVariables() {
		return variables;
	}

	public void setVariables(final List<VariableXBean> variables) {
		this.variables = variables;
	}

	public InputConfig toConfig() {
		if (isTemplate()) {
			throw new ConfigurationException("Tried to use abstract bean \"" + getName() + "\"");
		}
		validate();
		return createConfig();
	}
	
	protected Map<String, Object> getVariablesConfig() {
		final Map<String, Object> result = new HashMap<>();
		if (variables != null) {
			for (final VariableXBean item : variables) {
				result.put(item.getName(), item.getValue());
			}
		}
		return result;
	}
	
	protected abstract InputConfig createConfig();
}
