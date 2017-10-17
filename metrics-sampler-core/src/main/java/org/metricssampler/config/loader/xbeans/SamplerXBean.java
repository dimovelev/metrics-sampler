package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

import java.util.List;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

/**
 * Base class for sampler XBeans.
 */
public abstract class SamplerXBean extends TemplatableXBean {
	@XStreamAsAttribute
	private Integer interval;

	@XStreamAsAttribute
	private Boolean ignored;

	@XStreamAsAttribute
	private Boolean disabled;

	public abstract SamplerConfig toConfig(Map<String, InputConfig> inputs, Map<String, OutputConfig> outputs, Map<String, List<SelectorConfig>> selectorGroups, Map<String, Object> globalVariables);

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(final Integer interval) {
		this.interval = interval;
	}

	public Boolean getIgnored() {
		return ignored;
	}

	public void setIgnored(final Boolean ignored) {
		this.ignored = ignored;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(final Boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	protected void validate() {
		super.validate();
		if (isInstantiatable()) {
			greaterThanZero(this, "interval", getInterval());
		}
	}
}
