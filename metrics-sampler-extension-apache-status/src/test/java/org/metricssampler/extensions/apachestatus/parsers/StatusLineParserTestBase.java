package org.metricssampler.extensions.apachestatus.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public abstract class StatusLineParserTestBase {
	protected StatusLineParser testee;
	protected Map<MetricName, MetricValue> metrics;
	protected long timestamp;

	@Before
	public void setup() {
		testee = createTestee();
		metrics = new HashMap<MetricName, MetricValue>();
		timestamp = System.currentTimeMillis();
	}

	protected abstract StatusLineParser createTestee();

	protected void assertMetric(final String name, final Object expectedValue) {
		final MetricValue metricValue = metrics.get(new SimpleMetricName(name, null));
		assertNotNull("Metric named \"" + name + "\" not found in " + metrics.keySet(), metricValue);
		assertEquals("Metric \"" + name + "\" has wrong value", expectedValue, metricValue.getValue());
		assertEquals("Timestamp not used for metric \"" + name + "\"", timestamp, metricValue.getTimestamp());
	}

	protected void parseSuccess(final String line) {
		final boolean result = testee.parse(line, metrics, timestamp);
		assertTrue("Parser must be able to parse line \"" + line + "\"", result);
	}

	protected void parseFailure(final String line) {
		final boolean result = testee.parse(line, metrics, timestamp);

		assertFalse("Parser must not be able to parse line \"" + line + "\"", result);
		assertTrue("Parser should not add any metrics if it cannot parse the line", metrics.isEmpty());
	}
}
