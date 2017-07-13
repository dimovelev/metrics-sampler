package org.metricssampler.extensions.graphite;

import org.junit.Test;
import org.metricssampler.reader.MetricValue;

import static org.junit.Assert.assertEquals;

public class GraphiteMetricsWriterTest {
	@Test
	public void serializeValueWithPrefix() {
		final GraphiteMetricsWriter testee = new GraphiteMetricsWriter(new GraphiteOutputConfig("name", false, "host", 2811, "prefix_"));
		final long timestamp = System.currentTimeMillis();
		
		final String result = testee.serializeValue("this is the name", new MetricValue(timestamp, "28.11"));
		
		assertEquals("prefix_this_is_the_name 28.11 "+(timestamp/1000)+"\n", result);
	}

	@Test
	public void serializeValueWithoutPrefix() {
		final GraphiteMetricsWriter testee = new GraphiteMetricsWriter(new GraphiteOutputConfig("name", false, "host", 2811, null));
		final long timestamp = System.currentTimeMillis();
		
		final String result = testee.serializeValue("this is the name", new MetricValue(timestamp, "28.11"));

		assertEquals("this_is_the_name 28.11 "+(timestamp/1000)+"\n", result);
	}
	
}
