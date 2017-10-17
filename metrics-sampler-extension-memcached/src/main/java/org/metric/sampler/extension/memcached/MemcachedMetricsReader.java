package org.metric.sampler.extension.memcached;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.Metrics;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

import static org.metricssampler.util.SocketUtils.createAndConnect;

public class MemcachedMetricsReader extends AbstractMetricsReader<MemcachedInputConfig> implements BulkMetricsReader {
	private final Charset ASCII_CHARSET = Charset.forName("ASCII");

	public MemcachedMetricsReader(MemcachedInputConfig config) {
		super(config);
	}

	@Override
	public void open() throws MetricReadException {
        // we will do that when reading the metrics
	}

	@Override
	public void close() {
        // we will do that when reading the metrics
	}

	@Override
	public Metrics readAllMetrics() throws MetricReadException {
        try(final Socket socket = createAndConnect(config.getHost(), config.getPort(), config.getSocketOptions())) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), ASCII_CHARSET));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ASCII_CHARSET));
            final Metrics result = new Metrics();
            fetchStatistics("general", reader, writer, result);
            quit(writer);
            return result;
        } catch (IOException e) {
            throw new MetricReadException(e);
        }
	}

    protected void fetchStatistics(String type, BufferedReader reader, BufferedWriter writer, Metrics metrics) throws IOException {
        final String command = "stats" + (type.equals("general") ?  "" : " " + type);
        writer.write(command + "\n");
        writer.flush();
        final long timestamp = System.currentTimeMillis();
        String line;
        final String prefix = "stats." + type + ".";
        while ( (line = reader.readLine()) != null) {
            if (line.startsWith("STAT ")) {
                String[] cols = line.split(" ");
                if (cols.length == 3) {
                    final String key = cols[1];
                    final String value = cols[2];
                    metrics.add(prefix + key, timestamp, value);
                } else {
                    logger.warn("Failed to parse line \"" + line + "\". Skipping.");
                }
            }
            if (line.equals("END") || line.equals("ERROR")) {
                break;
            }
        }
    }

    protected void quit(BufferedWriter writer) throws IOException {
        writer.write("quit\n");
        writer.flush();
    }
}
