package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

@XStreamAlias("entry")
public class EntryXBean extends XBean {
	@XStreamAsAttribute
	private String key;

	@XStreamAsAttribute
	private String value;

	public String getKey() {
		return key;
	}
	public void setKey(final String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(final String value) {
		this.value = value;
	}
	public void validate() {
		notEmpty(this, "key", getKey());
	}
}
