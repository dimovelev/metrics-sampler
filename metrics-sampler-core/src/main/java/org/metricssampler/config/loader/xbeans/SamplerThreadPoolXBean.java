package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.ThreadPoolConfig;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;

@XStreamAlias("thread-pool")
public class SamplerThreadPoolXBean extends SharedResourceXBean {
	@XStreamAsAttribute
	private Integer size;

	@XStreamAsAttribute
	@XStreamAlias("keep-alive-time")
	private Integer keepAliveTime;
	
	@XStreamAsAttribute
	@XStreamAlias("max-size")
	private Integer maxSize;
	
	public Integer getSize() {
		return size;
	}

	public void setSize(final Integer size) {
		this.size = size;
	}

	public Integer getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(final Integer keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(final Integer maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	protected void validate() {
		super.validate();
		greaterThanZero(this, "size", getSize());
	}

	@Override
	protected SharedResourceConfig createConfig() {
		final int coreSize = getSize();
		final int maxSize = getMaxSize() != null ? getMaxSize() : -1;
		final int keepAliveTime = getKeepAliveTime() != null ? getKeepAliveTime() : -1;
		return new ThreadPoolConfig(getName(), isIgnored(), coreSize, maxSize, keepAliveTime);
	}
	
}
