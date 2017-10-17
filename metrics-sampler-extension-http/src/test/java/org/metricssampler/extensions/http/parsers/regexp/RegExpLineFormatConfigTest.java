package org.metricssampler.extensions.http.parsers.regexp;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.Metrics;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class RegExpLineFormatConfigTest {
	private RegExpLineFormat testee;
	private Metrics values;
	private long timestamp;

	@Before
	public void setUp() {
		testee = new RegExpLineFormat(Pattern.compile("\\s*(\\S+)\\s*=\\s*(\\S+)\\s*"), 1, 2);
		values = new Metrics();
		timestamp = System.currentTimeMillis();
	}

	protected void assertSingleMetric(final String name, final String value) {
		assertEquals(1, values.size());
		for (final Metric entry : values) {
			assertEquals(name, entry.getName().getName());
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
