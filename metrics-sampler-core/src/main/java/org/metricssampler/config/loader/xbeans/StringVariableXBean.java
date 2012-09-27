package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("string")
public class StringVariableXBean extends VariableXBean {
	@XStreamAsAttribute
	private String value;

	@Override
	public String getValue() {
		this.validate();
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty("value", "string variable", value);
	}
}
