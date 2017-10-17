package org.metricssampler.extensions.base;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegExpMetricsSelectorTest {
	private RegExpMetricsSelector testee;
	private BulkMetricsReader bulkReader;
	private MetaDataMetricsReader metadataReader;
	private Map<String, Object> variables;
	
	@Before
	public void setup() {
		RegExpSelectorConfig config = new RegExpSelectorConfig(".+\\.(.+)\\..+", null, "${prefix}.whatever.${name[1]}");
		testee = new RegExpMetricsSelector(config);
		variables = new HashMap<>();
		variables.put("prefix", "PREFIX");
		testee.setVariables(variables);
		bulkReader = mock(BulkMetricsReader.class);
		metadataReader = mock(MetaDataMetricsReader.class);
	}

	@Test
	public void readMetricsBulk() {
		MetricValue aaaValue = new MetricValue(System.currentTimeMillis(), "11");
		MetricValue bbbValue = new MetricValue(System.currentTimeMillis(), "28");
		Metrics metrics = new Metrics(
				new Metric(new SimpleMetricName("ignored-prefix.aaa.ignored-suffix", "whatever"), aaaValue),
				new Metric(new SimpleMetricName("ignored-prefix.bbb.ignored-suffix", "whatever"), bbbValue),
				new Metric(new SimpleMetricName("ignored-prefix.too-short", "whatever"), new MetricValue(System.currentTimeMillis(), "0"))
		);
		when(bulkReader.readAllMetrics()).thenReturn(metrics);
		
		Metrics result = testee.readMetrics(bulkReader);
		
		assertEquals(2, result.size());
		assertEquals(aaaValue, result.get("PREFIX.whatever.aaa").get().getValue());
		assertEquals(bbbValue, result.get("PREFIX.whatever.bbb").get().getValue());
	}
	
	@Test
	public void readMetricsMetaData() {
		MetricName aaaName = new SimpleMetricName("ignored-prefix.aaa.ignored-suffix", "whatever");
		MetricName bbbName = new SimpleMetricName("ignored-prefix.bbb.ignored-suffix", "whatever");
		MetricName ignoredName = new SimpleMetricName("ignored-prefix.too-short", "whatever");
		when(metadataReader.getMetaData()).thenReturn(new MetricsMetaData(Arrays.asList(aaaName, bbbName, ignoredName)));
		MetricValue aaaValue = new MetricValue(System.currentTimeMillis(), "11");
		when(metadataReader.readMetric(aaaName)).thenReturn(aaaValue);
		MetricValue bbbValue = new MetricValue(System.currentTimeMillis(), "28");
		when(metadataReader.readMetric(bbbName)).thenReturn(bbbValue);
		
		Metrics result = testee.readMetrics(metadataReader);
		
		assertEquals(2, result.size());
		assertEquals(aaaValue, result.get("PREFIX.whatever.aaa").get().getValue());
		assertEquals(bbbValue, result.get("PREFIX.whatever.bbb").get().getValue());
	}

}
