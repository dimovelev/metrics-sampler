package org.metricssampler.extensions.exec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class ExecMetricsReaderTest {
	private ExecMetricsReader testee;
	private final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
	
	@Before
	public void setup() {
		testee = new ExecMetricsReader(mock(ExecInputConfig.class));
		result.clear();
	}

	@Test
	public void parseMetricFromLine() {
		testee.parseMetricFromLine(result, "a.b.c.d=123");
		assertEquals(1, result.size());
		assertMetric("a.b.c.d", "123");
	}

	@Test
	public void parseMetricFromLineWithTimestamp() {
		final long now = System.currentTimeMillis();
		testee.parseMetricFromLine(result, now + ":a.b.c.d=123");
		assertEquals(1, result.size());
		assertMetric(now, "a.b.c.d", "123");
	}

	@Test
	public void parseMetricFromLineInvalid() {
		testee.parseMetricFromLine(result, "INVALID");
		assertTrue("Expected no metrics: " + result, result.isEmpty());
	}

	@Test
	public void parseMetricFromLineInvalidWithTimestamp() {
		final long now = System.currentTimeMillis();
		testee.parseMetricFromLine(result, now + ":INVALID");
		assertTrue("Expected no metrics: " + result, result.isEmpty());
	}

	protected void assertMetric(final String name, final String value) {
		assertMetric(-1L, name, value);
	}
	
	protected void assertMetric(final long timestamp, final String name, final String value) {
		for (final Entry<MetricName, MetricValue> entry : result.entrySet()) {
			if (entry.getKey().getName().equals(name)) {
				if (timestamp != -1L) {
					assertEquals("Timestamp of metric " + name, timestamp, entry.getValue().getTimestamp());
				}
				assertEquals("Value of metric " + name, value, entry.getValue().getValue());
				return;
			}
		}
		fail("Metric " + name + " not found: " + result);
	}
}
