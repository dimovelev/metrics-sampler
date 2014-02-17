package org.metricssampler.extensions.http.parsers.regexp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class RegExpLineFormatConfigTest {
	private RegExpLineFormat testee;
	private Map<MetricName, MetricValue> values;
	private long timestamp;

	@Before
	public void setUp() {
		testee = new RegExpLineFormat(Pattern.compile("\\s*(\\S+)\\s*=\\s*(\\S+)\\s*"), 1, 2);
		values = new HashMap<MetricName, MetricValue>();
		timestamp = System.currentTimeMillis();
	}

	protected void assertSingleMetric(final String name, final String value) {
		assertEquals(1, values.size());
		for (final Entry<MetricName, MetricValue> entry : values.entrySet()) {
			assertEquals(name, entry.getKey().getName());
			assertEquals(value, entry.getValue().getValue());
			assertEquals(timestamp, entry.getValue().getTimestamp());
			return;
		}
		fail("Could not find metric named \"" + name + "\" in " + values);
	}

	protected boolean parse(final String line) {
		return testee.parse(values, timestamp, line);
	}

	@Test
	public void testParse() {
		final boolean result = parse(" metric1 = 123 ");
		assertTrue(result);
		assertSingleMetric("metric1", "123");
	}

	@Test
	public void testIllegal() {
		final boolean result = parse(" metric1 : 123 ");
		assertFalse(result);
	}

}
