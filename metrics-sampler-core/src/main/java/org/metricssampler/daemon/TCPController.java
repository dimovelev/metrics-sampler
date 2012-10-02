package org.metricssampler.daemon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.IOUtils;
import org.metricssampler.service.Bootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPController implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String SAMPLER_COMMAND_PREFIX = "sampler ";
	private static final String SAMPLER_COMMAND_SUFFIX_START = " start";
	private static final String SAMPLER_COMMAND_SUFFIX_STOP = " stop";
	private static final String SAMPLER_COMMAND_SUFFIX_SAMPLE = " sample";

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
			executeOnMatchingTasks(name, writer, new SamplerTaskAction() {
				@Override
				public void execute(final SamplerTask task, final BufferedWriter writer) throws IOException {
					logger.info("Disabling sampler \"{}\"", task.getName());
					task.disable();
					writer.write("Sampler \"" + task.getName() + "\" disabled\n");
				}
			});
		} else if (line.endsWith(SAMPLER_COMMAND_SUFFIX_START)) {
			final String name = line.substring(SAMPLER_COMMAND_PREFIX.length(), line.length()-SAMPLER_COMMAND_SUFFIX_START.length());
			executeOnMatchingTasks(name, writer, new SamplerTaskAction() {
				@Override
				public void execute(final SamplerTask task, final BufferedWriter writer) throws IOException {
					logger.info("Enabling sampler \"{}\"", task.getName());
					task.enable();
					writer.write("Sampler \"" + task.getName() + "\" enabled\n");
				}
			});
		} else if (line.endsWith(SAMPLER_COMMAND_SUFFIX_SAMPLE)) {
			final String name = line.substring(SAMPLER_COMMAND_PREFIX.length(), line.length()-SAMPLER_COMMAND_SUFFIX_SAMPLE.length());
			executeOnMatchingTasks(name, writer, new SamplerTaskAction() {
				@Override
				public void execute(final SamplerTask task, final BufferedWriter writer) throws IOException {
					logger.info("Enabling sampler \"{}\" for one-time sampling", task.getName());
					task.enableOnce();
					writer.write("Sampler \"" + task.getName() + "\" enabled for one-time sampling\n");
				}
			});
		} else {
			writer.write("Invalid syntax. Use \"sampler <name> [start|stop]\"\n");
		}
		writer.flush();
	}
	
	private void executeOnMatchingTasks(final String expression, final BufferedWriter writer, final SamplerTaskAction action) throws IOException {
		try {
			final List<SamplerTask> matchingTasks = findMatchingTasks(expression);
			if (matchingTasks.isEmpty()) {
				writer.write("No samplers found matching regular expression \"" + expression + "\"\n");
			} else {
				for (final SamplerTask task : matchingTasks) {
					action.execute(task, writer);
				}
			}
		} catch (final PatternSyntaxException e) {
			writer.write("Could not compile sampler name regular expression \"" + expression +"\": "+e.getMessage()+"\n");
		}
	}

	private interface SamplerTaskAction {
		void execute(SamplerTask task, BufferedWriter writer) throws IOException; 
	}
	
	private List<SamplerTask> findMatchingTasks(final String expression) {
		final List<SamplerTask> result = new LinkedList<SamplerTask>();
		final Pattern nameExpression = Pattern.compile(expression);
		for (final Entry<String, SamplerTask> entry : tasks.entrySet()) {
			if (nameExpression.matcher(entry.getKey()).matches()) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
