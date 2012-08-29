package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import org.jmxsampler.config.PlaceholderConfig;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class PlaceholderXBean {
	@XStreamAsAttribute
	private String key;
	
	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}
	
	protected void validate() {
		notEmpty("key", "placeholder", getKey());
	}
	
	public abstract PlaceholderConfig toConfig();
}
