package org.metricssampler.extensions.apachestatus;

import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.metricssampler.extensions.apachestatus.parsers.GenericLineParser;
import org.metricssampler.extensions.apachestatus.parsers.ModQosParser;
import org.metricssampler.extensions.apachestatus.parsers.ScoreboardParser;
import org.metricssampler.extensions.apachestatus.parsers.StatusLineParser;
import org.metricssampler.reader.BaseHttpMetricsReader;
import org.metricssampler.reader.Metrics;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ApacheStatusMetricsReader extends BaseHttpMetricsReader<ApacheStatusInputConfig> {
	private final List<StatusLineParser> lineParsers = Arrays.asList(new ModQosParser(), new ScoreboardParser(), new GenericLineParser());

	public ApacheStatusMetricsReader(final ApacheStatusInputConfig config) {
		super(config);
	}

    @Override
    protected void processResponse(HttpUriRequest request, HttpResponse response) throws Exception {
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
		    try(final InputStreamReader reader = streamEntity(entity)) {
		    	final LineIterator lines = new LineIterator(reader);
				try {
					values = new Metrics();
					final long timestamp = System.currentTimeMillis();
					while (lines.hasNext()) {
						final String line = lines.next();
						parseLine(line, timestamp);
					}
				} finally {
					LineIterator.closeQuietly(lines);
				}
		    }
		} else {
			values = new Metrics();
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
