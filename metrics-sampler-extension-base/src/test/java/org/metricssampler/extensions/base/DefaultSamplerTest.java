package org.metricssampler.extensions.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.Metrics;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.writer.MetricsWriter;

import static org.mockito.Mockito.*;

public class DefaultSamplerTest {
	private DefaultSampler testee;
	private BulkMetricsReader bulkReader;
	private MetricsWriter writer1;
	private MetricsWriter writer2;
	private MetricsSelector transformer1;
	private MetricsSelector transformer2;
	private DefaultSamplerConfig config;
	
	@Before
	public void setup() {
		bulkReader = mock(BulkMetricsReader.class);
		
		writer1 = mock(MetricsWriter.class);
		writer2 = mock(MetricsWriter.class);
		
		transformer1 = mock(MetricsSelector.class);
		transformer2 = mock(MetricsSelector.class);
		
		config = mock(DefaultSamplerConfig.class);
		testee = new DefaultSampler(config, bulkReader);
		testee.addWriter(writer1);
		testee.addWriter(writer2);
		testee.addSelector(transformer1);
		testee.addSelector(transformer2);
		SamplerStats.set(new SamplerStats());
	}
	
	@After
	public void cleanup() {
		SamplerStats.unset();
	}
	
	@Test
	public void sampleBulk() {
		when(transformer1.readMetrics(bulkReader)).thenReturn(new Metrics());
		when(transformer2.readMetrics(bulkReader)).thenReturn(new Metrics());

		testee.sample();

		verify(bulkReader, times(1)).open();
		verify(bulkReader, times(1)).close();

		verify(transformer1, times(1)).readMetrics(bulkReader);
		verify(transformer2, times(1)).readMetrics(bulkReader);
		
		verify(writer1, times(1)).open();
		verify(writer1, times(1)).close();
		
		verify(writer2, times(1)).open();
		verify(writer2, times(1)).close();
	}
}
