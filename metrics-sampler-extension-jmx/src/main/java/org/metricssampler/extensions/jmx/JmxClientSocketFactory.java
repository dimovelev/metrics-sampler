package org.metricssampler.extensions.jmx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.server.RMISocketFactory;

import org.metricssampler.config.SocketOptionsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RMI socket factory makes socket timeout and other low-level TCP/IP options configurable. 
 */
public class JmxClientSocketFactory extends RMISocketFactory {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final SocketOptionsConfig config;
	
	public JmxClientSocketFactory(final SocketOptionsConfig config) {
		if (config == null) {
			throw new NullPointerException("Parameter config may not be null");
		}
		this.config = config;
	}

	@Override
	public Socket createSocket(final String host, final int port) throws IOException {
		logger.debug("Creating socket to " + host + ":" + port +" with "+config);
		final Socket result = new Socket();
		if (config.hasSoTimeout()) {
			result.setSoTimeout(config.getSoTimeout());
		}
		if (config.hasSndBuffSize()) {
			result.setSendBufferSize(config.getSndBuffSize());
		}
		if (config.hasRcvBuffSize()) {
			result.setReceiveBufferSize(config.getRcvBuffSize());
		}
		result.setKeepAlive(config.isKeepAlive());
		final InetAddress addr = InetAddress.getByName(host);
	    final SocketAddress endpoint = new InetSocketAddress(addr, port);
	    logger.debug("Connecting socket");
	    result.connect(endpoint, config.getConnectTimeout());
	    logger.debug("Socket connected");
		return result;
	}

	@Override
	public ServerSocket createServerSocket(final int port) throws IOException {
		return null;
	}

}
