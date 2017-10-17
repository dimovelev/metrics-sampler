package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Include further configuration files using a glob pattern 
 */
@XStreamAlias("include")
public class IncludeXBean extends XBean {
	/**
	 * A glob pattern for the configuration files to include. Relative to the location of the root configuration file.
	 */
	@XStreamAsAttribute
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}
	
}
