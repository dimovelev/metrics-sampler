package org.metricssampler.extensions.webmethods.parser;

import org.apache.commons.io.IOUtils;
import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.Metrics;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Base class for parsers of files that start with a 3-line header containing the timestamp on the second line. The file is read into a list
 * of lines in the beginning the they are processed one by one by the subclasses.
 */
public abstract class AbstractFileWithHeaderParser extends AbstractFileParser {
	/**
	 * The prefix to use for each metric discovered on the current line. This would be a prefix corresponding to the section / subsection
	 * that we are in. <code>null</code> if we are at the top level.
	 */
	protected String lastSection;

	protected AbstractFileWithHeaderParser(final WebMethodsInputConfig config, final String prefix) {
		super(config, prefix);
	}

	@Override
	protected void doParse(final InputStream stream, final Metrics metrics) throws IOException, ParseException {
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

	protected abstract void parseLine(Metrics metrics, List<String> lines, int lineNum, String line, long timestamp);

}
