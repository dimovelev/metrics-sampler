package org.metricssampler.extensions.http.parsers.regexp;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public class RegExpLineFormat {
	private final Pattern pattern;
	private final int nameGroupIndex;
	private final int valueGroupIndex;

	public RegExpLineFormat(final Pattern pattern, final int nameGroupIndex, final int valueGroupIndex) {
		this.pattern = pattern;
		this.nameGroupIndex = nameGroupIndex;
		this.valueGroupIndex = valueGroupIndex;
	}

	/**
	 * @return the regular expression used to parse the line
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return The index of the regex capturing group containing the metric name. The first one has the index 1.
	 */
	public int getNameGroupIndex() {
		return nameGroupIndex;
	}

	/**
	 * @return The index of the regex capturing group containing the metric value. The first one has the index 1.
	 */
	public int getValueGroupIndex() {
		return valueGroupIndex;
	}

	public boolean parse(final Map<MetricName, MetricValue> values, final long timestamp, final String line) {
		final Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			final String name = matcher.group(nameGroupIndex);
			final String value = matcher.group(valueGroupIndex);
			values.put(new SimpleMetricName(name, null), new MetricValue(timestamp, value));
			return true;
		} else {
			return false;
		}
	}
}
