package org.metricssampler.extensions.exec;

import org.apache.commons.io.IOUtils;
import org.metricssampler.reader.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Execute a process on each sample and read one metric per line in the form {@code [<timestamp>:]<name>=<value>} from its standard output and error.
 */
public class ExecMetricsReader extends AbstractMetricsReader<ExecInputConfig> implements BulkMetricsReader {
	private final ProcessBuilder processBuilder;
	private Process process = null;

	public ExecMetricsReader(final ExecInputConfig config) {
		super(config);
		processBuilder = createProcessBuilder(config);
	}

	protected ProcessBuilder createProcessBuilder(final ExecInputConfig config) {
		final List<String> command = new ArrayList<>(config.getArguments().size() + 1);
		command.add(config.getCommand());
		command.addAll(config.getArguments());
		final ProcessBuilder result = new ProcessBuilder(command);
		result.environment().putAll(config.getEnvironment());
		if (config.getDirectory() != null) {
			result.directory(config.getDirectory());
		}
		result.redirectErrorStream(true);
		return result;
	}

	@Override
	public void open() {
		try {
			process = processBuilder.start();
		} catch (final IOException e) {
			throw new OpenMetricsReaderException(e);
		}
	}

	@Override
	public void close() {
		if (process != null) {
			process.destroy();
			process = null;
		}
	}

	@Override
	public Metrics readAllMetrics() {
		assert process != null;
		final Metrics result = new Metrics();
		addMetricsFromOutput(result);
		return result;
	}

	protected void addMetricsFromOutput(final Metrics result) {
		InputStream inputStream = null;
		try {
			inputStream = process.getInputStream();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while ( (line = reader.readLine()) != null) {
					parseMetricFromLine(result, line);
				}
			} catch (final IOException e) {
				throw new MetricReadException(e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	protected void parseMetricFromLine(final Metrics result, final String line) {
		final int timestampEndIdx = line.indexOf(':');
		if (timestampEndIdx > 0) {
			// starts with a timestamp
			final String timestampStr = line.substring(0, timestampEndIdx);
			try {
				final long timestamp = Long.parseLong(timestampStr);
				parseMetric(timestamp, result, line.substring(timestampEndIdx+1));
			} catch (final NumberFormatException e) {
				logger.warn("Failed to parse value \"{}\" as timestamp", timestampStr);
			}
		} else {
			// no timestamp => use now
			parseMetric(System.currentTimeMillis(), result, line);
		}
	}

	protected void parseMetric(final long timestamp, final Metrics result, final String line) {
		final String[] cols = line.split("=", 2);
		if (cols.length == 2) {
			result.add(cols[0], timestamp, cols[1]);
		} else {
			logger.warn("Failed to parse line \"{}\". It should be of the form [<timestamp>:]<metric-name>=<metric-value>", line);
		}
	}

}
