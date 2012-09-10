package org.jmxsampler.service;

import org.jmxsampler.config.SelectorConfig;
import org.jmxsampler.config.InputConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.OutputConfig;

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
