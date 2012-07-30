package org.jmxsampler.extensions.graphite.writer;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.jmxsampler.config.loader.xbeans.ValidationUtils.validPort;

import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.config.loader.xbeans.WriterXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("graphite-writer")
public class GraphiteWriterXBean extends WriterXBean {
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
	public WriterConfig toConfig() {
		validate();
		return new GraphiteWriterConfig(getName(), getHost(), getPort(), getPrefix());
	}

}
