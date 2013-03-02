package org.metricssampler.extensions.webmethods.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public final class MetricsAssert {
	private MetricsAssert() {
	}

	public static void assertMetric(final Map<MetricName, MetricValue> metrics, final long timestamp, final String name, final String value) {
		final MetricValue actual = metrics.get(new SimpleMetricName(name, null));
		assertNotNull("Metric \"" + name + "\" not found in " + metrics.keySet().toString(), actual);
		assertEquals("Timestamp for metric \"" + name +"\" wrong", timestamp, actual.getTimestamp());
		assertEquals("Value for metric \"" + name + "\" wrong", value, actual.getValue());
	}
}
