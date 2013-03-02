package org.metricssampler.extensions.webmethods.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public abstract class AbstractFileWithHeaderParser extends AbstractFileParser {
	protected String lastSection;

	protected AbstractFileWithHeaderParser(final WebMethodsInputConfig config, final String prefix) {
		super(config, prefix);
	}

	@Override
	protected void doParse(final InputStream stream, final Map<MetricName, MetricValue> metrics) throws IOException, ParseException {
		final List<String> lines = IOUtils.readLines(stream);
		int lineNum = 0;
		long timestamp = -1L;
		lastSection = null;
		for (final String line : lines) {
			logger.debug(line);
			if (lineNum == 1) {
				final Date date = parseTimestampLine(line);
				timestamp = date.getTime();
			} else if (lineNum > 3) {
				parseLine(metrics, lines, lineNum, line, timestamp);
			}
			lineNum++;
		}
	}

	protected abstract void parseLine(Map<MetricName, MetricValue> metrics, List<String> lines, int lineNum, String line, long timestamp);

}
