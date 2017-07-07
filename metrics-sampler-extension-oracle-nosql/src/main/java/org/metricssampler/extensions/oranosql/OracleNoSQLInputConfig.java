package org.metricssampler.extensions.oranosql;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.LoginConfig;
import org.metricssampler.util.Preconditions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class OracleNoSQLInputConfig extends InputConfig {
	private final List<HostConfig> hosts;
	private final Optional<String> storeName;
	private final Optional<Path> trustFile;
	private final Optional<LoginConfig> login;

	public OracleNoSQLInputConfig(final String name, final Map<String, Object> variables, final List<HostConfig> hosts, Optional<String> storeName, Optional<Path> trustFile, Optional<LoginConfig> login) {
		super(name, variables);
		checkArgumentNotNullNorEmpty(hosts, "hosts");
		this.hosts = hosts;
		this.storeName = storeName;
		this.trustFile = trustFile;
		this.login = login;
		if (login.isPresent() || trustFile.isPresent() || storeName.isPresent()) {
			Preconditions.checkArgumentPresent(login, "login");
			Preconditions.checkArgumentPresent(trustFile, "trustFile");
			if (!Files.exists(trustFile.get())) {
				throw new IllegalArgumentException("The trust file [" + trustFile.get().toAbsolutePath() + "] does not exist");
			}
			Preconditions.checkArgumentPresent(storeName, "storeName");
		}
	}

	public List<HostConfig> getHosts() {
		return hosts;
	}

	public boolean isAuthenticationRequired() {
		return login.isPresent();
	}

	public LoginConfig getLogin() {
		return login.orElse(null);
	}

	public Path getTrustFile() {
		return trustFile.orElse(null);
	}

	public String getStoreName() {
		return storeName.orElse(null);
	}

	public static class HostConfig {
		private final String host;
		private final int port;

		public HostConfig(final String host, final int port) {
			this.host = host;
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (!getClass().equals(obj.getClass())) {
				return false;
			}
			final HostConfig that = (HostConfig) obj;
			return port == that.port && host.equals(that.host);
		}

		@Override
		public int hashCode() {
			return host.hashCode() + port;
		}

		@Override
		public String toString() {
			return host + ":" + port;
		}
	}
}
