package org.jmxsampler.service;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.transformer.MetricsTransformer;
import org.jmxsampler.writer.MetricsWriter;

public interface ObjectFactory {
	MetricsWriter newWriter(WriterConfig config);
	MetricsReader newReader(ReaderConfig config);
	MetricsTransformer newTransformer(MappingConfig config);
	Sampler newSampler(SamplerConfig config);
}
