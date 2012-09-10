package org.jmxsampler.extensions.graphite;

import static org.junit.Assert.assertEquals;

import org.jmxsampler.extensions.graphite.GraphiteMetricsWriter;
import org.jmxsampler.extensions.graphite.GraphiteOutputConfig;
import org.jmxsampler.reader.MetricValue;
import org.junit.Test;

public class GraphiteMetricsWriterTest {
	@Test
	public void serializeValueWithPrefix() {
		final GraphiteMetricsWriter testee = new GraphiteMetricsWriter(new GraphiteOutputConfig("name", "host", 2811, "prefix_"));
		final long timestamp = System.currentTimeMillis();
		
		final String result = testee.serializeValue("this is the name", new MetricValue(timestamp, "28.11"));
		
		assertEquals("prefix_this_is_the_name 28.11 "+(timestamp/1000)+"\n", result);
	}

	@Test
	public void serializeValueWithoutPrefix() {
		final GraphiteMetricsWriter testee = new GraphiteMetricsWriter(new GraphiteOutputConfig("name", "host", 2811, null));
		final long timestamp = System.currentTimeMillis();
		
		final String result = testee.serializeValue("this is the name", new MetricValue(timestamp, "28.11"));

		assertEquals("this_is_the_name 28.11 "+(timestamp/1000)+"\n", result);
	}
	
}
