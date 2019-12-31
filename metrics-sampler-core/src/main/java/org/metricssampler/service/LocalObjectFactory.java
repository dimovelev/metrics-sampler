package org.metricssampler.service;

import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.ValueTransformerConfig;

/**
 * A factory intended to be implemented by extensions.
 */
public interface LocalObjectFactory extends ObjectFactory {
    void setGlobalFactory(GlobalObjectFactory factory);

    /**
     * @param config the output configuration
     * @return {@code true} if the extension can create a writer for the given output configuration.
     */
    boolean supportsOutput(OutputConfig config);

    /**
     * @param config the input configuration
     * @return {@code true} if the extension can create a reader for the given input configuration.
     */
    boolean supportsInput(InputConfig config);

    /**
     * @param config the selector configuration
     * @return {@code true} if the extension can create a selector for the given selector configuration.
     */
    boolean supportsSelector(SelectorConfig config);

    /**
     * @param config the sampler configuration
     * @return {@code true} if the extension can create a sampler for the given sampler configuration.
     */
    boolean supportsSampler(SamplerConfig config);

    /**
     * @param config the shared resource configuration
     * @return {@code true} if the extension can create a shared resource for the given shared resource configuration.
     */
    boolean supportsSharedResource(SharedResourceConfig config);

    /**
     * @param config the value transformer configuration
     * @return {@code true} if the extension can create a value transformer for the given configuration.
     */
    boolean supportsValueTransformer(ValueTransformerConfig config);
}
