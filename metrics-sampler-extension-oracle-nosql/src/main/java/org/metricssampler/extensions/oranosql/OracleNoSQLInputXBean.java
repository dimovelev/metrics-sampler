package org.metricssampler.extensions.oranosql;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.LoginConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

@XStreamAlias("oracle-nosql")
public class OracleNoSQLInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String hosts;

	@XStreamAsAttribute
	private String store;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;

	@XStreamAsAttribute
	private String trustFile;

	public String getStore() {
		return store;
	}

	public OracleNoSQLInputXBean setStore(final String store) {
		this.store = store;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public OracleNoSQLInputXBean setUsername(final String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public OracleNoSQLInputXBean setPassword(final String password) {
		this.password = password;
		return this;
	}

	public String getTrustFile() {
		return trustFile;
	}

	public OracleNoSQLInputXBean setTrustFile(final String trustFile) {
		this.trustFile = trustFile;
		return this;
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
		notEmpty(this, "hosts", getHosts());
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
				validPort(this, "port", Integer.parseInt(spec[1]));
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

		final Optional<String> storeName = store != null ? Optional.of(store) : Optional.empty();

		final Optional<Path> trustPath = trustFile != null ? Optional.of(Paths.get(trustFile)) : Optional.empty();

		final Optional<LoginConfig> login = username != null && password != null ? Optional.of(new LoginConfig(username, password)) : Optional.empty();

		return new OracleNoSQLInputConfig(getName(), getVariablesConfig(), hostConfigs, storeName, trustPath, login);
	}
}
