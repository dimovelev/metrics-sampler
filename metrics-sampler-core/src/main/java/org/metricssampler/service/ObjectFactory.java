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

	/**
	 * @param config The configuration of the shared resource
	 * @param suspended whether the shared resource should be suspended right after start. This is usually done for
	 *                  configuration checking and will be implemented according to the type of shared resource.
	 * @return a new shared resource from the given configuration
	 */
	SharedResource newSharedResource(SharedResourceConfig config, boolean suspended);
}
