package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class VariableXBean {
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
		final Map<String, Object> result = new HashMap<String, Object>();
		if (variables != null) {
			for (final VariableXBean variable : variables) {
				result.put(variable.getName(), variable.getValue());
			}
		}
		return result;
	}
}
