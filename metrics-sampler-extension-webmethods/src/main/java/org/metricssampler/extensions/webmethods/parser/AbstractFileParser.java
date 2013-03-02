package org.metricssampler.extensions.webmethods.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileParser {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final WebMethodsInputConfig config;
	private final String prefix;

	protected AbstractFileParser(final WebMethodsInputConfig config, final String prefix) {
		this.config = config;
		this.prefix = prefix;
	}

	public Map<MetricName, MetricValue> parse(final InputStream stream) throws IOException, ParseException {
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		doParse(stream, result);
		return result;
	}

	protected Date parseTimestampLine(final String line) throws ParseException {
		final int idx = line.indexOf("  ");
		if (idx > 0) {
			return config.getDateFormat().parse(line.substring(0, idx));
		} else {
			return config.getDateFormat().parse(line);
		}
	}

	protected String parseMemory(final String value) {
		final int spaceIdx = value.indexOf(' ');
		if (spaceIdx > 0) {
			final long val = Long.parseLong(value.substring(0, spaceIdx));
			final String unit = value.substring(spaceIdx+1);
			if ("KB".equals(unit)) {
				return Long.toString(val*1024);
			} else if ("MB".equals(unit)) {
				return Long.toString(val*1024*1024);
			} else if ("GB".equals(unit)) {
				return Long.toString(val*1024*1024*1024);
			} else {
				logger.warn("Unknown unit \"{}\" in memory value \"{}\"", unit, value);
				return Long.toString(val);
			}
		} else {
			return value;
		}
	}

	protected String parseValue(final String name, final String value) {
		if (name.contains("Memory")) {
			return parseMemory(value);
		} else if (name.contains("Uptime")) {
			// TODO
			return value;
		} else {
			return value;
		}
	}

	protected MetricName createMetricName(final StringBuilder name) {
		final StringBuilder result = new StringBuilder(prefix).append('.');
		for (int i=0; i<name.length(); i++) {
			final char c = name.charAt(i);
			if (c != ' ' && c != '(' && c != ')' && c != '=') {
				if (c == ':') {
					result.append('.');
				} else {
					result.append(c);
				}
			}
		}
		return new SimpleMetricName(result.toString(), null);
	}

	protected abstract void doParse(final InputStream stream, final Map<MetricName, MetricValue> metrics) throws IOException, ParseException;

	public abstract boolean canParseEntry(final String name);
}
