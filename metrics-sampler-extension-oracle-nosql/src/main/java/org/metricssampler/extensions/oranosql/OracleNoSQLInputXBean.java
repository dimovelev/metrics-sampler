package org.metricssampler.extensions.oranosql;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("oracle-nosql")
public class OracleNoSQLInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String store;

	@XStreamAsAttribute
	private String hosts;

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(final String hosts) {
		this.hosts = hosts;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty("store", "oracle-nosql", getStore());
		notEmpty("hosts", "oracle-nosql", getHosts());
	}
	
	@Override
	protected InputConfig createConfig() {
		final String[] hostsArray = getHosts().split(" ");
		return new OracleNoSQLInputConfig(getName(), getVariablesConfig(), getStore(), hostsArray);
	}
}
