package org.metricssampler.service;

import org.metricssampler.config.*;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.values.ValueTransformer;
import org.metricssampler.writer.MetricsWriter;

public interface ObjectFactory {
	MetricsWriter newWriterForOutput(OutputConfig config);
	MetricsReader newReaderForInput(InputConfig config);
	MetricsSelector newSelector(SelectorConfig config);
	ValueTransformer newValueTransformer(ValueTransformerConfig config);
	Sampler newSampler(SamplerConfig config);
	SharedResource newSharedResource(SharedResourceConfig config);
}
