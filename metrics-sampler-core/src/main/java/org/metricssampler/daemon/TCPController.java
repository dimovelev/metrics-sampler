package org.metricssampler.daemon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPController implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String SAMPLER_COMMAND_PREFIX = "sampler ";
	private static final String SAMPLER_COMMAND_SUFFIX_START = " start";
	private static final String SAMPLER_COMMAND_SUFFIX_STOP = " stop";

	private final ServerSocket serverSocket;
	private final ExecutorService executor;
	private final Bootstrapper bootstrapper;
	private final Map<String, SamplerTask> tasks;
	
	public TCPController(final Bootstrapper bootstrapper, final ScheduledThreadPoolExecutor executor, final Map<String, SamplerTask> tasks) {
		this.bootstrapper = bootstrapper;
		this.executor = executor;
		this.tasks = tasks;
		this.serverSocket = createServerSocket();
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
			BufferedReader reader = null;
			BufferedWriter writer = null;
			try {
				socket = serverSocket.accept();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				String line;
				while ( (line = reader.readLine()) != null) {
					if ("shutdown".equals(line)) {
						shutdown(reader, writer);
					} else if ("status".equals(line)) {
						status(writer);
						break;
					} else {
						if (line != null && line.startsWith(SAMPLER_COMMAND_PREFIX)) {
							sampler(line, writer);
							break;
						} else {
							logger.warn("Unknown command \"{}\"", line);
						}
					}
				}
				reader.close();
				writer.close();
				socket.close();
			} catch (final IOException e) {
				logger.warn("Failed to accept connection from client", e);
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(writer);
				IOUtils.closeQuietly(socket);
				continue;
			}
		}
	}

	private void shutdown(final BufferedReader reader, final BufferedWriter writer) {
		logger.info("Shutdown command received");
		logger.info("Shutting down executor service");
		executor.shutdown();
		try {
			logger.debug("Waiting for the executor service to gracefully shutdown");
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			logger.warn("Thread pool failed to gracefully shutdown within 20 seconds. Forcing shtudown");
		}
		logger.info("Executor service terminated");
		IOUtils.closeQuietly(writer);
		System.exit(0);
	}

	private void status(final BufferedWriter writer) throws IOException {
		logger.debug("Status command received. Responding with ok.");
		writer.write("ok\n");
		writer.flush();
	}

	private void sampler(final String line, final BufferedWriter writer) throws IOException {
		if (line.endsWith(SAMPLER_COMMAND_SUFFIX_STOP)) {
			final String name = line.substring(SAMPLER_COMMAND_PREFIX.length(), line.length()-SAMPLER_COMMAND_SUFFIX_STOP.length());
			final SamplerTask task = tasks.get(name);
			if (task != null) {
				logger.info("Disabling sampler \"{}\"", name);
				task.disable();
				writer.write("Sampler \"" + name + "\" disabled\n");
			} else {
				writer.write("Sampler named \"" + name + "\" not found\n");
			}
		} else if (line.endsWith(SAMPLER_COMMAND_SUFFIX_START)) {
			final String name = line.substring(SAMPLER_COMMAND_PREFIX.length(), line.length()-SAMPLER_COMMAND_SUFFIX_START.length());
			final SamplerTask task = tasks.get(name);
			if (task != null) {
				logger.info("Enabling sampler \"{}\"", name);
				task.enable();
				writer.write("Sampler \"" + name + "\" enabled\n");
			} else {
				writer.write("Sampler named \"" + name + "\" not found\n");
			}
		} else {
			writer.write("Invalid syntax. Use \"sampler <name> [start|stop]\"\n");
		}
		writer.flush();
	}

}
