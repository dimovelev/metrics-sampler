package org.metricssampler.extensions.graphite;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.loader.xbeans.OutputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("graphite")
public class GraphiteOutputXBean extends OutputXBean {
	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private int port;

	@XStreamAsAttribute
	private String prefix;

	public String getHost() {
		return host;
	}
	public void setHost(final String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(final int port) {
		this.port = port;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty("host", "graphite writer", getHost());
		validPort("port", "graphite writer", getPort());
	}
	@Override
	public OutputConfig toConfig() {
		validate();
		return new GraphiteOutputConfig(getName(), getHost(), getPort(), getPrefix());
	}

}
