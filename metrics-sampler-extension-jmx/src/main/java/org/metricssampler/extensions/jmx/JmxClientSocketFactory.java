package org.metricssampler.extensions.jmx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RMI socket factory makes socket timeout and other low-level TCP/IP options configurable. 
 */
public class JmxClientSocketFactory extends RMISocketFactory {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final int soTimeout;
	private final boolean keepAlive;

	public JmxClientSocketFactory(final int soTimeout, final boolean keepAlive) {
		this.soTimeout = soTimeout;
		this.keepAlive = keepAlive;
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException {
		logger.debug("Creating socket to " + host + ":" + port + " and socket timeout " + soTimeout);
		final Socket result = new Socket(host, port);
		result.setSoTimeout(soTimeout);
		result.setKeepAlive(keepAlive);
		return result;
	}

	@Override
	public ServerSocket createServerSocket(final int port) throws IOException {
		return null;
	}

}
