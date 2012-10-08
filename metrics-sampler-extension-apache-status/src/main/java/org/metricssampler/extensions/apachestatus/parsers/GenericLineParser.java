package org.metricssampler.extensions.apachestatus.parsers;

import java.util.Map;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse any line as a key value pair separated by colon optionally followed by spaces.
 */
public class GenericLineParser implements StatusLineParser {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean parse(final String line, final Map<MetricName, MetricValue> metrics, final long timestamp) {
		if (line != null) {
			final String[] cols = line.split(":\\s*", 2);
			if (cols.length == 2) {
				final String name = cols[0].replace(' ', '_');
				metrics.put(new SimpleMetricName(name, cols[0]), new MetricValue(timestamp, cols[1]));
			} else {
				logger.debug("Ignoring response line \"{}\"", metrics);
			}
		}
		return true;
	}

}
