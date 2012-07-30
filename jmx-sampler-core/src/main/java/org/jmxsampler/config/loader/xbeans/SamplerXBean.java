package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

import java.util.List;
import java.util.Map;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class SamplerXBean {
	@XStreamAsAttribute
	private int interval;

	@XStreamAsAttribute
	private boolean disabled;

	public abstract SamplerConfig toConfig(Map<String, ReaderConfig> readers, Map<String, WriterConfig> writers, Map<String, List<MappingConfig>> mappingTemplates);

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
