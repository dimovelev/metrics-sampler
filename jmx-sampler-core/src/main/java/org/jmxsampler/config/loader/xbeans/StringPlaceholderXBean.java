package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import org.jmxsampler.config.StringPlaceholderConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("string-placeholder")
public class StringPlaceholderXBean extends PlaceholderXBean {
	@XStreamAsAttribute
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	
	@Override
	protected void validate() {
		super.validate();
		notEmpty("value", "string placeholder", getKey());
	}

	@Override
	public StringPlaceholderConfig toConfig() {
		validate();
		return new StringPlaceholderConfig(getKey(), getValue());
	}
}
