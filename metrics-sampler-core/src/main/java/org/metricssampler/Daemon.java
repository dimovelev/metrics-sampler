package org.metricssampler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.metricssampler.config.Configuration;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Daemon {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Bootstrapper bootstrapper;

	private ScheduledThreadPoolExecutor executor;

	private ServerSocket serverSocket;
	
	public Daemon(final Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	public void start() {
		bindControlEndpoint();
		executor = setupThreadPool();
		startControlThread();
		scheduleSamplers();
	}

	private ScheduledThreadPoolExecutor setupThreadPool() {
		final Configuration config = bootstrapper.getConfiguration();
		logger.info("Starting thread pool executor with thread pool size: "+config.getPoolSize());
        return new ScheduledThreadPoolExecutor(config.getPoolSize());
	}

	private void startControlThread() {
		final Thread daemon = new Thread() {
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
								logger.info("Shutdown command received");
								logger.debug("Shutting down executor service");
								executor.shutdown();
								try {
									logger.debug("Waiting for the executor service to gracefully shutdown");
									executor.awaitTermination(20, TimeUnit.SECONDS);
								} catch (final InterruptedException e) {
									logger.warn("Thread pool failed to gracefully shutdown within 20 seconds. Forcing shtudown");
								}
								System.exit(0);
							} else if ("status".equals(line)) {
								logger.debug("Status command received. Responding with ok.");
								writer.write("ok\n");
								writer.flush();
								break;
							} else {
								logger.warn("Unknown command \"{}\"", line);
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
		};
		daemon.start();
	}

	private void bindControlEndpoint() {
		try {
			serverSocket = new ServerSocket();
			final InetSocketAddress endpoint = new InetSocketAddress(bootstrapper.getControlHost(), bootstrapper.getControlPort());
			serverSocket.bind(endpoint);
			logger.info("Bound control endpoint at {}:{}", bootstrapper.getControlHost(), bootstrapper.getControlPort());
		} catch (final IOException e) {
			logger.error("Failed to bind control endpoint at {}:{}", bootstrapper.getControlHost(), bootstrapper.getControlPort());
		}
	}

	private void scheduleSamplers() {
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			logger.info("Scheduling {} at fixed rate of {} seconds", sampler, sampler.getRate());
	        executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						sampler.sample();
					} catch (final RuntimeException e) {
						logger.warn("Sampler threw exception. Ignoring.", e);
					}
				}
			}, 0L, sampler.getRate(), TimeUnit.SECONDS);
		}
	}
}
