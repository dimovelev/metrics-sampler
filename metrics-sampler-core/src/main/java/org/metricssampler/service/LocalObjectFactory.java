package org.metricssampler.service;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

/**
 * A factory intended to be implemented by extensions.
 */
public interface LocalObjectFactory extends ObjectFactory {
	void setGlobalFactory(GlobalObjectFactory factory);
	boolean supportsOutput(OutputConfig config);
	boolean supportsInput(InputConfig config);
	boolean supportsSelector(SelectorConfig config);
	boolean supportsSampler(SamplerConfig config);
}
