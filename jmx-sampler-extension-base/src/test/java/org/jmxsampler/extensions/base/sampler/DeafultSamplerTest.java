package org.jmxsampler.extensions.base.sampler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;

import org.jmxsampler.config.PlaceholderConfig;
import org.jmxsampler.reader.BulkMetricsReader;
import org.jmxsampler.transformer.MetricsTransformer;
import org.jmxsampler.writer.MetricsWriter;
import org.junit.Before;
import org.junit.Test;

public class DeafultSamplerTest {
	private DefaultSampler testee;
	private BulkMetricsReader bulkReader;
	private MetricsWriter writer1;
	private MetricsWriter writer2;
	private MetricsTransformer transformer1;
	private MetricsTransformer transformer2;
	
	@Before
	public void setup() {
		bulkReader = mock(BulkMetricsReader.class);
		
		writer1 = mock(MetricsWriter.class);
		writer2 = mock(MetricsWriter.class);
		
		transformer1 = mock(MetricsTransformer.class);
		transformer2 = mock(MetricsTransformer.class);
		
		testee = new DefaultSampler(bulkReader, new LinkedList<PlaceholderConfig>());
		testee.addWriter(writer1);
		testee.addWriter(writer2);
		testee.addTransformer(transformer1);
		testee.addTransformer(transformer2);
	}
	
	@Test
	public void sampleBulk() {
		testee.sample();
		verify(bulkReader, times(1)).open();
		verify(bulkReader, times(1)).close();

		verify(transformer1, times(1)).transformMetrics(bulkReader);
		verify(transformer2, times(1)).transformMetrics(bulkReader);
		
		verify(writer1, times(1)).open();
		verify(writer1, times(1)).close();
		
		verify(writer2, times(1)).open();
		verify(writer2, times(1)).close();
	}
}
