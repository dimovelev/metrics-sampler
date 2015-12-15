package org.metric.sampler.extension.memcached;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;
import org.metricssampler.config.loader.xbeans.SocketOptionsXBean;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

@XStreamAlias("memcached")
public class MemcachedInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;

	@XStreamAlias("socket-options")
	private SocketOptionsXBean socketOptions;

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(final Integer port) {
		this.port = port;
	}

	public SocketOptionsXBean getSocketOptions() {
		return socketOptions;
	}

	public void setSocketOptions(SocketOptionsXBean socketOptions) {
		this.socketOptions = socketOptions;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "host", getHost());
		validPort(this, "port", getPort());
	}

	@Override
	protected InputConfig createConfig() {
        return new MemcachedInputConfig(getName(), getVariablesConfig(), getHost(), getPort(),
                getSocketOptions() != null ? getSocketOptions().toConfig() : null);
	}

}
