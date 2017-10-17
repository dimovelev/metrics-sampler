package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

@XStreamAlias("string")
public class StringVariableXBean extends VariableXBean {
	@XStreamAsAttribute
	private String value;

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "value", value);
	}
}
