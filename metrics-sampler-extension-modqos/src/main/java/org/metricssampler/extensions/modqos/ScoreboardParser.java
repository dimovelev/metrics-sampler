package org.metricssampler.extensions.modqos;

import static org.metricssampler.util.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public class ScoreboardParser {
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
	public Map<MetricName, MetricValue> parse(final String line) {
		checkArgument(line.startsWith(SCOREBOARD_PREFIX), "Not a scoreboard line");
		final long timestamp = System.currentTimeMillis();
		final int[] counts = new int[chars.length()];
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		final String scoreboard = line.substring(SCOREBOARD_PREFIX.length());
		for (int i=0; i<scoreboard.length(); i++) {
			counts[chars.indexOf(scoreboard.charAt(i))]++;
		}
		result.put(new SimpleMetricName("workers.total_count", "Total number of workers"), new MetricValue(timestamp, scoreboard.length()));
		for (int i=0; i<counts.length; i++) {
			result.put(NAMES[i], new MetricValue(timestamp, counts[i]));
		}
		return result;
	}
}
