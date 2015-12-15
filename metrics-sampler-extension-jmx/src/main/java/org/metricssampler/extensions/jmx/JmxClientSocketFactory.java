package org.metricssampler.extensions.jmx;

import org.metricssampler.config.SocketOptionsConfig;
import org.metricssampler.util.SocketUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * A RMI socket factory makes socket timeout and other low-level TCP/IP options configurable. 
 */
public class JmxClientSocketFactory extends RMISocketFactory {
	private final SocketOptionsConfig config;
	
	public JmxClientSocketFactory(final SocketOptionsConfig config) {
		if (config == null) {
			throw new NullPointerException("Parameter config may not be null");
		}
		this.config = config;
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException {
		return SocketUtils.createAndConnect(host, port, config);
	}

	@Override
	public ServerSocket createServerSocket(final int port) throws IOException {
		return null;
	}

}
