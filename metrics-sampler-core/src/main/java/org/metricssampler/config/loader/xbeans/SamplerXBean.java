package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

import java.util.List;
import java.util.Map;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Base class for sampler XBeans.
 */
public abstract class SamplerXBean extends TemplatableXBean {
	@XStreamAsAttribute
	private Integer interval;

	@XStreamAsAttribute
	private boolean disabled;

	public abstract SamplerConfig toConfig(Map<String, InputConfig> inputs, Map<String, OutputConfig> outputs, Map<String, List<SelectorConfig>> selectorGroups, Map<String, Object> globalVariables);

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(final Integer interval) {
		this.interval = interval;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	protected void validate() {
		super.validate();
		if (isInstantiatable()) {
			greaterThanZero("interval", "sampler", getInterval());
		}
	}
}
