package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.SharedResourceConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("shared-resource")
public abstract class SharedResourceXBean extends NamedXBean {
	private Boolean ignored;

	public Boolean getIgnored() {
		return ignored;
	}

	public void setIgnored(final Boolean ignored) {
		this.ignored = ignored;
	}

	@Override
	protected void validate() {
		super.validate();
	}
	
	public SharedResourceConfig toConfig() {
		validate();
		return createConfig();
	}

	protected abstract SharedResourceConfig createConfig();
}
