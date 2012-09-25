package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import org.metricssampler.config.Variable;

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
		notEmpty("name", "variable", getName());
	}
	
	public abstract Variable toConfig();
}
