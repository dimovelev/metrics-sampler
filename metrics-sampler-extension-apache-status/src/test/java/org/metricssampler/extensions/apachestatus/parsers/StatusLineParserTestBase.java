package org.metricssampler.extensions.apachestatus.parsers;

import org.junit.Before;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;

import java.util.Optional;

import static org.junit.Assert.*;

public abstract class StatusLineParserTestBase {
	protected StatusLineParser testee;
	protected Metrics metrics;
	protected long timestamp;

	@Before
	public void setup() {
		testee = createTestee();
		metrics = new Metrics();
		timestamp = System.currentTimeMillis();
	}

	protected abstract StatusLineParser createTestee();

	protected void assertMetric(final String name, final Object expectedValue) {
		final Optional<Metric> metric = metrics.get(name);
		assertTrue("Metric named \"" + name + "\" not found in " + metrics.getNames(), metric.isPresent());
		MetricValue metricValue = metric.get().getValue();
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
