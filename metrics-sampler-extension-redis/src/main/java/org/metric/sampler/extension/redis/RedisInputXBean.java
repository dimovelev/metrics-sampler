package org.metric.sampler.extension.redis;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("redis")
public class RedisInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String host;
	
	@XStreamAsAttribute
	private Integer port;
	
	@XStreamAsAttribute
	private String password;
	
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

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty("host", "redis reader", getHost());
		validPort("port", "redis reader", getPort());
	}

	@Override
	protected InputConfig createConfig() {
		validate();
		return new RedisInputConfig(getName(), getVariablesConfig(), getHost(), getPort(), getPassword());
	}

}
