package org.metricssampler.extensions.graphite;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.metricssampler.reader.MetricValue;
import org.metricssampler.writer.MetricWriteException;
import org.metricssampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send metrics to graphite. This class is not thread safe and should not be used by multiple samplers.
 */
public class GraphiteMetricsWriter implements MetricsWriter {
	private final Logger logger;

	private final GraphiteOutputConfig config;

	private Socket socket;
	private Writer writer;

	public GraphiteMetricsWriter(final GraphiteOutputConfig config) {
		this.config = config;
		this.logger = LoggerFactory.getLogger("writer."+config.getName());
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
	public void write(final Map<String, MetricValue> metrics) {
		assertIsConnected();
		final StringBuilder builder = new StringBuilder();
		for (final Map.Entry<String, MetricValue> entry : metrics.entrySet()) {
			final MetricValue value = entry.getValue();
			final String msg = serializeValue(entry.getKey(), value);
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

	protected String serializeValue(final String name, final MetricValue value) {
		final long timestamp = value.getTimestamp()/1000;
		final String prefixedName = (config.getPrefix() != null ? config.getPrefix() : "") + name;
		final String graphiteName = prefixedName.replaceAll(" ", "_");
		return graphiteName + " " + value.getValue()+" " + timestamp + "\n";
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
