package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

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
