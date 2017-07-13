package org.metricssampler.extensions.http.parsers.regexp;

import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.extensions.http.HttpResponseParser;
import org.metricssampler.reader.Metrics;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * Parse an HTTP response using a list of regular expressions. The first one that matches wins.
 */
public class RegExpHttpResponseParser implements HttpResponseParser {

	private final List<RegExpLineFormat> lineFormats;

	public RegExpHttpResponseParser(final List<RegExpLineFormat> lineFormats) {
		this.lineFormats = Collections.unmodifiableList(lineFormats);
	}

	public List<RegExpLineFormat> getLineFormats() {
		return lineFormats;
	}

	@Override
	public Metrics parse(final HttpResponse response, final HttpEntity entity, final InputStreamReader reader) {
		final Metrics result = new Metrics();
		final LineIterator lines = new LineIterator(reader);
		try {
			final long timestamp = System.currentTimeMillis();
			while (lines.hasNext()) {
				parseLine(result, timestamp, lines.next());
			}
		} finally {
			lines.close();
		}
		return result;
	}

	protected boolean parseLine(final Metrics result, final long timestamp, final String line) {
		for (final RegExpLineFormat regexp : lineFormats) {
			if (regexp.parse(result, timestamp, line)) {
				return true;
			}
		}
		return false;
	}

}
