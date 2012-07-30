package org.jmxsampler.service;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;

/**
 * A factory intended to be implemented by extensions.
 */
public interface LocalObjectFactory extends ObjectFactory {
	void setGlobalFactory(GlobalObjectFactory factory);
	boolean supportsWriter(WriterConfig config);
	boolean supportsReader(ReaderConfig config);
	boolean supportsTransformer(MappingConfig config);
	boolean supportsSampler(SamplerConfig config);
}
