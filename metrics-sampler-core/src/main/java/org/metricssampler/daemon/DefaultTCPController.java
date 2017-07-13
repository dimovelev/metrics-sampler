package org.metricssampler.daemon;

import org.metricssampler.daemon.commands.ControlCommand;
import org.metricssampler.daemon.commands.DefaultControlCommandFactory;
import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class DefaultTCPController implements TCPController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ServerSocket serverSocket;
	private final Bootstrapper bootstrapper;
	private final Map<String, SamplerTask> tasks;
	private final Map<String, SharedResource> sharedResources;
	private BufferedReader clientReader = null;
	private BufferedWriter clientWriter = null;

	private final ControlCommandParser commandParser;

	public DefaultTCPController(final Bootstrapper bootstrapper, final Map<String, SamplerTask> tasks, final Map<String, SharedResource> sharedResources) {
		this.bootstrapper = bootstrapper;
		this.tasks = tasks;
		this.sharedResources = sharedResources;
		this.serverSocket = createServerSocket();
		this.commandParser = new ControlCommandParser(new DefaultControlCommandFactory(this));
	}

	@Override
	public Bootstrapper getBootstrapper() {
		return bootstrapper;
	}

	@Override
	public Map<String, SamplerTask> getTasks() {
		return tasks;
	}

	@Override
	public Map<String, SharedResource> getSharedResources() {
		return sharedResources;
	}

	private ServerSocket createServerSocket() {
		final String host = bootstrapper.getControlHost();
		final int port = bootstrapper.getControlPort();
		try {
			final ServerSocket result = new ServerSocket();
			final InetSocketAddress endpoint = new InetSocketAddress(host, port);
			result.bind(endpoint);
			logger.info("Bound control endpoint at {}:{}", host, port);
			return result;
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to bind control endpoint at " +  host + ":" + port, e);
		}
	}

	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				final String line = clientReader.readLine();
				if (line != null) {
					final ControlCommand command = commandParser.parse(line);
					command.execute();
				}
				clientReader.close();
				clientWriter.close();
				clientWriter = null;
				socket.close();
			} catch (final IOException e) {
				logger.warn("Failed to accept connection from client", e);
				closeQuietly(clientReader);
				closeQuietly(clientWriter);
				closeQuietly(socket);
				continue;
			}
		}
	}

	@Override
	public BufferedReader getClientReader() {
		return clientReader;
	}

	@Override
	public BufferedWriter getClientWriter() {
		return clientWriter;
	}
}
