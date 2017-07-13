package org.metricssampler.extensions.webmethods.parser;

import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public final class MetricsAssert {
	private MetricsAssert() {
	}

	public static void assertMetric(final Metrics metrics, final long timestamp, final String name, final String value) {
		final Optional<Metric> metric = metrics.get(name);
		assertTrue("Metric \"" + name + "\" not found in " + metrics.getNames(), metric.isPresent());
		final  MetricValue actual = metric.get().getValue();
		assertEquals("Timestamp for metric \"" + name +"\" wrong", timestamp, actual.getTimestamp());
		assertEquals("Value for metric \"" + name + "\" wrong", value, actual.getValue());
	}
}
