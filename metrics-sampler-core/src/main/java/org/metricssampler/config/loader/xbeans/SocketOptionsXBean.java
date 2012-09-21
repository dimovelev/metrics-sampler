package org.metricssampler.config.loader.xbeans;

import org.metricssampler.config.SocketOptionsConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("socket-options")
public class SocketOptionsXBean {
	@XStreamAlias("so-timeout")
	@XStreamAsAttribute
	private Integer soTimeout;
	
	@XStreamAlias("keep-alive")
	@XStreamAsAttribute
	private Boolean keepAlive = false;

	public Integer getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(final Integer soTimeout) {
		this.soTimeout = soTimeout;
	}

	public Boolean getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(final Boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public SocketOptionsConfig toConfig() {
		return new SocketOptionsConfig(soTimeout, keepAlive);
	}
}
