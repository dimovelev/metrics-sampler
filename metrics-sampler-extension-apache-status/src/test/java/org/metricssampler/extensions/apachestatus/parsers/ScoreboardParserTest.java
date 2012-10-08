package org.metricssampler.extensions.apachestatus.parsers;

import org.junit.Test;

public class ScoreboardParserTest extends StatusLineParserTestBase {

	@Override
	protected StatusLineParser createTestee() {
		return new ScoreboardParser();
	}

	@Test
	public void parseWrongLine() {
		parseFailure("whatever: 1");
	}

	@Test
	public void parseMany() {
		parseSuccess("Scoreboard: _____W...WKLC..DKKRSSS__SLG.");

		assertMetric("workers.total_count", 28);
		assertMetric("workers.logging", 2);
		assertMetric("workers.gracefully_finishing", 1);
		assertMetric("workers.open_slot", 6);
		assertMetric("workers.sending_reply", 2);
		assertMetric("workers.starting_up", 4);
		assertMetric("workers.waiting_for_connection", 7);
		assertMetric("workers.dns_lookup", 1);
		assertMetric("workers.closing_connection", 1);
		assertMetric("workers.idle_cleanup", 0);
		assertMetric("workers.keepalive", 3);
	}

	@Test
	public void parseSimple() {
		parseSuccess("Scoreboard: ___W__..........................................................................................................................................................................................................................................................");

		assertMetric("workers.total_count", 256);
		assertMetric("workers.logging", 0);
		assertMetric("workers.gracefully_finishing", 0);
		assertMetric("workers.open_slot", 250);
		assertMetric("workers.sending_reply", 1);
		assertMetric("workers.starting_up", 0);
		assertMetric("workers.waiting_for_connection", 5);
		assertMetric("workers.dns_lookup", 0);
		assertMetric("workers.closing_connection", 0);
		assertMetric("workers.idle_cleanup", 0);
		assertMetric("workers.keepalive", 0);
	}
}
