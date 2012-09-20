package org.metricssampler.extensions.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxConnection {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JmxInputConfig config;
	private final JMXServiceURL url;
	private final Map<String, String> environment;

	private JMXConnector connector;
	private MBeanServerConnection serverConnection;

	public JmxConnection(final JmxInputConfig config) throws IOException {
		this.config = config;
		url = new JMXServiceURL(config.getUrl());
		environment = constructEnvironment();
	}

	private Map<String, String> constructEnvironment() {
		final Map<String, String> result = new HashMap<String, String>();
		if (config.getUsername() != null) {
			result.put(Context.SECURITY_PRINCIPAL, config.getUsername());
			result.put(Context.SECURITY_CREDENTIALS, config.getPassword());
		}
		result.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, config.getProviderPackages());
		return result;
	}

	public boolean isEstablished() {
		return serverConnection != null;
	}

	public void connect() throws IOException {
		if (!isEstablished()) {
			establishConnection();
		}
	}

	public void disconnect() {
		if (serverConnection != null) {
			serverConnection = null;
			try {
				logger.debug("Disconnecting");
				connector.close();
			} catch (final IOException e) {
				// Ignore
			}
			connector = null;
		}
	}
	protected void establishConnection() throws IOException {
		logger.debug("Connecting to " + config.getUrl());
		connector = JMXConnectorFactory.connect(url, environment);
		logger.debug("Getting MBean server connection");
		serverConnection = connector.getMBeanServerConnection();
		logger.debug("Connected");
	}

	public MBeanServerConnection getServerConnection() {
		assertEstablished();
		return serverConnection;
	}

	private void assertEstablished() {
		if (!isEstablished()) {
			throw new IllegalStateException("Connection not established");
		}
	}
}
