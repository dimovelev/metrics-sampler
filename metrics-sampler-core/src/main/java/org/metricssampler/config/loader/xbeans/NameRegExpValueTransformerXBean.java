package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.regex.Pattern;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

public abstract class NameRegExpValueTransformerXBean extends ValueTransformerXBean {
	@XStreamAsAttribute
	private String name;

	@Override
	protected void validate() {
		notEmpty(this, "name", name);
	}
	
	
	protected Pattern getNamePattern() {
		return Pattern.compile(name);
	}
}
