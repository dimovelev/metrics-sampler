package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.SharedResourceConfig;

@XStreamAlias("shared-resource")
public abstract class SharedResourceXBean extends NamedXBean {
	private Boolean ignored;

	public Boolean getIgnored() {
		return ignored;
	}

	public void setIgnored(final Boolean ignored) {
		this.ignored = ignored;
	}

	public boolean isIgnored() {
		return ignored == null ? false : ignored;
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
