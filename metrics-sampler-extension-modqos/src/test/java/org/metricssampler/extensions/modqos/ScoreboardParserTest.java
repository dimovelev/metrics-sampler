package org.metricssampler.extensions.modqos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class ScoreboardParserTest {
	private ScoreboardParser testee;
	
	@Before
	public void setup() {
		testee = new ScoreboardParser();
	}

	@Test
	public void parseMany() {
		final String line = "Scoreboard: _____W...WKLC..DKKRSSS__SLG.";
		final Map<MetricName, MetricValue> result = testee.parse(line);
		final Map<String, String> map = convertToMap(result);
		System.out.println(map);
		assertMetric(map, "total_count", 28);
		assertMetric(map, "logging", 2);
		assertMetric(map, "gracefully_finishing", 1);
		assertMetric(map, "open_slot", 6);
		assertMetric(map, "sending_reply", 2);
		assertMetric(map, "starting_up", 4);
		assertMetric(map, "waiting_for_connection", 7);
		assertMetric(map, "dns_lookup", 1);
		assertMetric(map, "closing_connection", 1);
		assertMetric(map, "idle_cleanup", 0);
		assertMetric(map, "keepalive", 3);
	}
	
	@Test
	public void parseSimple() {
		final String line = "Scoreboard: ___W__..........................................................................................................................................................................................................................................................";
		final Map<MetricName, MetricValue> result = testee.parse(line);
		final Map<String, String> map = convertToMap(result);
		System.out.println(map);
		assertMetric(map, "total_count", 256);
		assertMetric(map, "logging", 0);
		assertMetric(map, "gracefully_finishing", 0);
		assertMetric(map, "open_slot", 250);
		assertMetric(map, "sending_reply", 1);
		assertMetric(map, "starting_up", 0);
		assertMetric(map, "waiting_for_connection", 5);
		assertMetric(map, "dns_lookup", 0);
		assertMetric(map, "closing_connection", 0);
		assertMetric(map, "idle_cleanup", 0);
		assertMetric(map, "keepalive", 0);
	}

	private Map<String, String> convertToMap(final Map<MetricName, MetricValue> param) {
		final Map<String, String> result = new HashMap<String, String>(param.size());
		for (final Entry<MetricName, MetricValue> entry : param.entrySet()) {
			result.put(entry.getKey().getName(), entry.getValue().getValue().toString());
		}
		return result;
	}
	
	private void assertMetric(final Map<String, String> map, final String name, final int expectedValue) {
		final String key = "workers." + name;
		assertTrue("Metric \"" + key + "\" not found", map.containsKey(key));
		assertEquals("Metric \"" + key + "\" has wrong value", expectedValue, Integer.parseInt(map.get(key)));
	}
}
