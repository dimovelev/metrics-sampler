package org.jmxsampler.extensions.graphite.writer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.jmxsampler.writer.MetricWriteException;
import org.jmxsampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send metrics to graphite. This class is not thread safe and should not be used by multiple samplers.
 */
public class GraphiteMetricsWriter implements MetricsWriter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final GraphiteWriterConfig config;

	private Socket socket;
	private Writer writer;

	public GraphiteMetricsWriter(final GraphiteWriterConfig config) {
		this.config = config;
	}

	@Override
	public void open() throws MetricWriteException {
		if (!isConnected()) {
	        try {
				socket = new Socket(config.getHost(), config.getPort());
				writer = new OutputStreamWriter(socket.getOutputStream());
			} catch (final UnknownHostException e) {
				throw new MetricWriteException(e);
			} catch (final IOException e) {
				throw new MetricWriteException(e);
			}
		}
	}

	protected boolean isConnected() {
		return socket != null;
	}

	@Override
	public void close() throws MetricWriteException {
		if (isConnected()) {
			try {
				writer.close();
			} catch (final IOException e) {
				// Ignore
			}
			try {
				socket.close();
			} catch (final IOException e) {
				// Ignore
			}
			writer = null;
			socket = null;
		}
	}

	@Override
	public void write(final Map<String, Object> metrics) {
		assertIsConnected();
		final long timestamp = System.currentTimeMillis() / 1000;
		final StringBuilder builder = new StringBuilder();
		for (final Map.Entry<String, Object> entry : metrics.entrySet()) {
			final String msg = (config.getPrefix() != null ? config.getPrefix() : "") + entry.getKey() + " " + entry.getValue()+" " + timestamp + "\n";
			builder.append(msg);
		}
		try {
			logger.debug("Sending to graphite:\n"+builder.toString());
			writer.write(builder.toString());
			writer.flush();
		} catch (final IOException e) {
			throw new MetricWriteException(e);
		}
	}

	protected void assertIsConnected() {
		if (!isConnected()) {
			throw new IllegalStateException("Not connected");
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+config.getHost()+":"+config.getPort()+"]";
	}
}
