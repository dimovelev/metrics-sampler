package org.metricssampler.extensions.apachestatus.parsers;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;
import org.metricssampler.reader.SimpleMetricName;

/**
 * Parse the apache scoreboard status line
 */
public class ScoreboardParser implements StatusLineParser {
	public static final String SCOREBOARD_PREFIX = "Scoreboard: ";
	private static final String chars = "_SRWKDCLGI.";
	private static final MetricName[] NAMES = {
		new SimpleMetricName("workers.waiting_for_connection", "_: Waiting for Connection"),
		new SimpleMetricName("workers.starting_up", "S: Starting up"),
		new SimpleMetricName("workers.reading_request", "R: Reading Request"),
		new SimpleMetricName("workers.sending_reply", "W: Sending Reply"),
		new SimpleMetricName("workers.keepalive", "K: Keepalive (read)"),
		new SimpleMetricName("workers.dns_lookup", "D: DNS Lookup"),
		new SimpleMetricName("workers.closing_connection", "C: Closing connection"),
		new SimpleMetricName("workers.logging", "L: Logging"),
		new SimpleMetricName("workers.gracefully_finishing", "G: Gracefully finishing"),
		new SimpleMetricName("workers.idle_cleanup", "I: Idle cleanup of worker"),
		new SimpleMetricName("workers.open_slot", ".: Open slot with no current process")
	};

	@Override
	public boolean parse(final String line, final Metrics metrics, final long timestamp) {
		if (!line.startsWith(SCOREBOARD_PREFIX)) {
			return false;
		}
		final int[] counts = new int[chars.length()];
		final String scoreboard = line.substring(SCOREBOARD_PREFIX.length());
		for (int i=0; i<scoreboard.length(); i++) {
			counts[chars.indexOf(scoreboard.charAt(i))]++;
		}
		metrics.add("workers.total_count", "Total number of workers", timestamp, scoreboard.length());
		for (int i=0; i<counts.length; i++) {
			metrics.add(NAMES[i], new MetricValue(timestamp, counts[i]));
		}
		return true;
	}
}
