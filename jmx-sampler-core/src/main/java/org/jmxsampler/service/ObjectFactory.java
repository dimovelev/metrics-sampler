package org.jmxsampler.service;

import org.jmxsampler.config.SelectorConfig;
import org.jmxsampler.config.InputConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.selector.MetricsSelector;
import org.jmxsampler.writer.MetricsWriter;

public interface ObjectFactory {
	MetricsWriter newWriterForOutput(OutputConfig config);
	MetricsReader newReaderForInput(InputConfig config);
	MetricsSelector newSelector(SelectorConfig config);
	Sampler newSampler(SamplerConfig config);
}
