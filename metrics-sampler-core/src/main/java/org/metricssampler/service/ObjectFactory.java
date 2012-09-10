package org.metricssampler.service;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.writer.MetricsWriter;

public interface ObjectFactory {
	MetricsWriter newWriterForOutput(OutputConfig config);
	MetricsReader newReaderForInput(InputConfig config);
	MetricsSelector newSelector(SelectorConfig config);
	Sampler newSampler(SamplerConfig config);
}
