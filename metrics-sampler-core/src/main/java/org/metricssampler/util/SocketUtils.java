package org.metricssampler.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.metricssampler.config.SocketOptionsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketUtils {
    private static final Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    private SocketUtils() {
    }

    /**
     * Configure the socket with the given socket options. This should be done before the socket is connected. The
     * connection timeout is not configurable - it must be used when connecting the socket.
     *
     * @param socket  A not connected socket
     * @param options The socket options. If null, no socket options will be configured.
     * @throws SocketException in case some of the options failed to be set
     */
    public static void configureSocketOptions(Socket socket, SocketOptionsConfig options) throws SocketException {
        if (options != null) {
            if (options.isKeepAlive()) {
                socket.setKeepAlive(true);
            }
            if (options.hasSoTimeout()) {
                socket.setSoTimeout(options.getSoTimeout());
            }
            if (options.hasSndBuffSize()) {
                socket.setSendBufferSize(options.getSndBuffSize());
            }
            if (options.hasRcvBuffSize()) {
                socket.setReceiveBufferSize(options.getRcvBuffSize());
            }
        }
    }

    /**
     * Create, configure and connect a socket.
     *
     * @param address The address to connect to
     * @param options The socket options to configure. If null, no socket options will be configured
     * @return The configured and connected socket
     *
     * @throws IOException if the connection failed
     */
    public static Socket createAndConnect(SocketAddress address, SocketOptionsConfig options) throws IOException {
        final Socket result = new Socket();

        configureSocketOptions(result, options);

        logger.debug("Connecting socket");
        if (options != null && options.hasConnectTimeout()) {
            result.connect(address, options.getConnectTimeout());
        } else {
            result.connect(address);
        }
        logger.debug("Socket connected");

        return result;
    }

    public static Socket createAndConnect(String host, int port, SocketOptionsConfig options) throws IOException {
        final InetAddress addr = InetAddress.getByName(host);
        final SocketAddress endpoint = new InetSocketAddress(addr, port);
        return createAndConnect(endpoint, options);
    }
}
