package org.metricssampler.config.loader.xbeans;

import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.Variable;

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
		if (isAbstract()) {
			throw new ConfigurationException("Tried to use abstract bean \"" + getName() + "\"");
		}
		validate();
		return createConfig();
	}
	
	protected List<Variable> getVariablesConfig() {
		final List<Variable> result = new LinkedList<Variable>();
		if (variables != null) {
			for (final VariableXBean item : variables) {
				result.add(item.toConfig());
			}
		}
		return result;
	}
	
	protected abstract InputConfig createConfig();
}
