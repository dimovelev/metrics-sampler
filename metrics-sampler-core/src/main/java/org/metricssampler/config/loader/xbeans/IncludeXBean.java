package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("include")
public class IncludeXBean {
	@XStreamAsAttribute
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}
	
}
