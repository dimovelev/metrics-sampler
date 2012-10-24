package org.metricssampler.extensions.oranosql;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

import java.util.ArrayList;
import java.util.List;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("oracle-nosql")
public class OracleNoSQLInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String hosts;

	public String getHosts() {
		return hosts;
	}

	public void setHosts(final String hosts) {
		this.hosts = hosts;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty("hosts", "oracle-nosql", getHosts());
		final String[] hostsArray = getHosts().split(" ");
		if (hostsArray.length == 0) {
			throw new ConfigurationException("Specify at least one host configuration in hosts");
		}
		for (final String hostEntry : hostsArray) {
			final String[] spec = hostEntry.split(":");
			if (spec.length != 2) {
				throw new ConfigurationException("Invalid host specification: " + hostEntry+". Should be in the form <host>:<port>.");
			}
			try {
				validPort("port", "oracle-nosql", Integer.parseInt(spec[1]));
			} catch (final NumberFormatException e) {
				throw new ConfigurationException("Invalid port in the host specification: " + hostEntry);
			}
		}
	}

	@Override
	protected InputConfig createConfig() {
		final String[] hostSpecs = hosts.split(" ");
		final List<HostConfig> hostConfigs = new ArrayList<HostConfig>(hostSpecs.length);
		for (final String hostSpec : hostSpecs) {
			final String[] hostCols = hostSpec.split(":");
			hostConfigs.add(new HostConfig(hostCols[0], Integer.parseInt(hostCols[1])));
		}
		return new OracleNoSQLInputConfig(getName(), getVariablesConfig(), hostConfigs);
	}
}
