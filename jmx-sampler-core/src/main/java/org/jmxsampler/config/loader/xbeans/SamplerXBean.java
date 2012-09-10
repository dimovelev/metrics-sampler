package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

import java.util.List;
import java.util.Map;

import org.jmxsampler.config.InputConfig;
import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.config.Placeholder;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.SelectorConfig;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Base class for sampler XBeans.
 */
public abstract class SamplerXBean {
	@XStreamAsAttribute
	private int interval;

	@XStreamAsAttribute
	private boolean disabled;

	public abstract SamplerConfig toConfig(Map<String, InputConfig> inputs, Map<String, OutputConfig> outputs, Map<String, List<SelectorConfig>> selectorGroups, List<Placeholder> placeholders);

	public int getInterval() {
		return interval;
	}

	public void setInterval(final int interval) {
		this.interval = interval;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	protected void validate() {
		greaterThanZero("interval", "default sampler", getInterval());
	}
}
