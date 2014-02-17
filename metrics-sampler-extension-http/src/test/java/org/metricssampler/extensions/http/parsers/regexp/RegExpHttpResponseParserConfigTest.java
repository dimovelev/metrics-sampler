package org.metricssampler.extensions.http.parsers.regexp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class RegExpHttpResponseParserConfigTest {
	private RegExpHttpResponseParser testee;
	private Map<MetricName, MetricValue> values;
	private long timestamp;

	@Before
	public void setUp() {
		final RegExpLineFormat format1 = new RegExpLineFormat(Pattern.compile("\\s*(\\S+)\\s*=\\s*(\\S+)\\s*"), 1, 2);
		final RegExpLineFormat format2 = new RegExpLineFormat(Pattern.compile("\\s*(\\S+)\\s*:\\s*(\\S+)\\s*"), 1, 2);
		testee = new RegExpHttpResponseParser(Arrays.asList(format1, format2));
		values = new HashMap<>();
		timestamp = System.currentTimeMillis();
	}

	protected boolean parseLine(final String line) {
		return testee.parseLine(values, timestamp, line);
	}

	@Test
	public void testParseLineFirst() {
		final boolean parsed = parseLine("metric=28");
		assertTrue(parsed);
		assertEquals(1, values.size());
	}

	@Test
	public void testParseLineSecond() {
		final boolean parsed = parseLine("metric:28");
		assertTrue(parsed);
		assertEquals(1, values.size());
	}

	@Test
	public void testParseLineNone() {
		final boolean parsed = parseLine("metric 28");
		assertFalse(parsed);
		assertEquals(0, values.size());
	}

}
