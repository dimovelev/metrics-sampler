package org.metricssampler.extensions.apachestatus;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.extensions.apachestatus.parsers.GenericLineParser;
import org.metricssampler.extensions.apachestatus.parsers.ModQosParser;
import org.metricssampler.extensions.apachestatus.parsers.ScoreboardParser;
import org.metricssampler.extensions.apachestatus.parsers.StatusLineParser;
import org.metricssampler.reader.BaseHttpMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class ApacheStatusMetricsReader extends BaseHttpMetricsReader<ApacheStatusInputConfig> {
	private final List<StatusLineParser> lineParsers = Arrays.asList(new ModQosParser(), new ScoreboardParser(), new GenericLineParser());

	public ApacheStatusMetricsReader(final ApacheStatusInputConfig config) {
		super(config);
	}

	@Override
	protected void processResponse(final HttpResponse response) throws IOException {
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
		    final InputStreamReader reader = streamEntity(entity);
		    try {
				final LineIterator lines = new LineIterator(reader);
				try {
					values = new HashMap<MetricName, MetricValue>();
					final long timestamp = System.currentTimeMillis();
					while (lines.hasNext()) {
						final String line = lines.next();
						parseLine(line, timestamp);
					}
				} finally {
					lines.close();
				}
		    } finally {
		    	closeQuietly(reader);
		    }
		} else {
			values = Collections.emptyMap();
			logger.warn("Response was null. Response line: {}", response.getStatusLine());
		}
	}

	protected void parseLine(final String line, final long timestamp) {
		boolean parsed = false;
		for (final StatusLineParser lineParser : lineParsers) {
			parsed = parsed || lineParser.parse(line, values, timestamp);
			if (parsed) {
				break;
			}
		}
		if (!parsed) {
			logger.debug("Ignoring response line \"{}\" as I do not know how to parse it", line);
		}
	}
}
