package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.ThreadPoolConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("thread-pool")
public class ThreadPoolXBean extends SharedResourceXBean {
	@XStreamAsAttribute
	private Integer size;

	public Integer getSize() {
		return size;
	}

	public void setSize(final Integer size) {
		this.size = size;
	}

	@Override
	protected void validate() {
		super.validate();
		greaterThanZero(this, "size", getSize());
	}

	@Override
	protected SharedResourceConfig createConfig() {
		return new ThreadPoolConfig(getName(), isIgnored(), getSize());
	}
	
}
