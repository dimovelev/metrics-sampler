package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.ConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

public abstract class VariableXBean extends XBean {
	@XStreamAsAttribute
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	protected void validate() {
		notEmpty(this, "name", getName());
	}

	public abstract Object getValue();
	
	public static Map<String, Object> toMap(final List<VariableXBean> variables) {
		final Map<String, Object> result = new HashMap<>();
		if (variables != null) {
			for (final VariableXBean variable : variables) {
				variable.validate();
				final String variableName = variable.getName();
				if (result.containsKey(variableName)) {
					throw new ConfigurationException("Two variables with the same name " + variableName);
				} else {
					result.put(variableName, variable.getValue());
				}
			}
		}
		return result;
	}
}
