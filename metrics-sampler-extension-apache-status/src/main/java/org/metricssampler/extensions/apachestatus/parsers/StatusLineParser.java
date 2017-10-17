package org.metricssampler.extensions.apachestatus.parsers;

import org.metricssampler.reader.Metrics;

/**
 * A parser that can parse a single line of the response
 */
public interface StatusLineParser {
	/**
	 * Try to parse a single line of the response. This method is also called to lines that are not supported by the parser. In such cases
	 * the parser must return {@code false}.
	 *
	 * @param line the line to parse
	 * @param metrics the metrics to append to
	 * @param timestamp the timestamp to use for the metrics unless they contain their own timestamp
	 * @return {@code true} if the parser was responsible for parsing this response.
	 */
	boolean parse(String line, Metrics metrics, long timestamp);
}
