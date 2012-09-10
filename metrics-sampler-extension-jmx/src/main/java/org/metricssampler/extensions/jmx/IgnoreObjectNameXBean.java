package org.metricssampler.extensions.jmx;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("ignore-object-name")
public class IgnoreObjectNameXBean {
	@XStreamAsAttribute
	private String regexp;

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(final String regexp) {
		this.regexp = regexp;
	}
}
