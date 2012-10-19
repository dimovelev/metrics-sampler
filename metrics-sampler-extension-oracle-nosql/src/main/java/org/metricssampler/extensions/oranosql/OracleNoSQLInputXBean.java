package org.metricssampler.extensions.oranosql;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("oracle-nosql")
public class OracleNoSQLInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String store;

	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

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

	@Override
	protected void validate() {
		super.validate();
		notEmpty("store", "oracle-nosql", getStore());
		notEmpty("host", "oracle-nosql", getHost());
		validPort("port", "oracle-nosql", getPort());
	}

	@Override
	protected InputConfig createConfig() {
		return new OracleNoSQLInputConfig(getName(), getVariablesConfig(), getStore(), getHost(), getPort());
	}
}
