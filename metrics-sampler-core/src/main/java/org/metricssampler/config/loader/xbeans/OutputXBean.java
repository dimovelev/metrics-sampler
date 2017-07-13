package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.OutputConfig;

/**
 * Base class for output XBeans.
 */
public abstract class OutputXBean extends NamedXBean {
	@XStreamAlias("default")
	@XStreamAsAttribute
	private Boolean default_;
	
	public Boolean getDefault_() {
		return default_;
	}

	public void setDefault_(final Boolean default_) {
		this.default_ = default_;
	}

	public boolean isDefault() {
		return default_ != null ? default_ : false;
	}
	
	public abstract OutputConfig toConfig();
}
