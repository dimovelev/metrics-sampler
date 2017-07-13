package org.metricssampler.extensions.jmx;

import org.metricssampler.resources.SamplerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class JmxConnection {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JmxInputConfig config;
	private final JMXServiceURL url;
	private final Map<String, Object> environment;

	private JMXConnector connector;
	private MBeanServerConnection serverConnection;

	public JmxConnection(final JmxInputConfig config) throws IOException {
		this.config = config;
		url = new JMXServiceURL(config.getUrl());
		environment = constructEnvironment();
	}

	private Map<String, Object> constructEnvironment() {
		final Map<String, Object> result = new HashMap<>();
		result.putAll(config.getConnectionProperties());
		if (config.getUsername() != null) {
			String[]  credentials = new String[] {config.getUsername(), config.getPassword()};
			result.put(JMXConnector.CREDENTIALS, credentials);
		}
		result.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, config.getProviderPackages());
		if (config.hasSocketOptions()) {
			final JmxClientSocketFactory sf = new JmxClientSocketFactory(config.getSocketOptions());
			result.put("com.sun.jndi.rmi.factory.socket", sf);
		}
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

	protected void establishConnection() throws IOException {
		SamplerStats.get().incConnectCount();
		logger.debug("Connecting to ", config.getUrl());
		connector = JMXConnectorFactory.connect(url, environment);
		logger.debug("Getting MBean server connection");
		serverConnection = connector.getMBeanServerConnection();
		logger.debug("Connected");
	}

	public void disconnect() {
		if (serverConnection != null) {
			SamplerStats.get().incDisconnectCount();
			serverConnection = null;
			logger.debug("Disconnecting");
			closeQuietly(connector);
			connector = null;
		}
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
