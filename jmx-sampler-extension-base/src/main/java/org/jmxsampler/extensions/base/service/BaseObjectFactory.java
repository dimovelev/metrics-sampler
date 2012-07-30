package org.jmxsampler.extensions.base.service;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.extensions.base.sampler.DefaultSampler;
import org.jmxsampler.extensions.base.sampler.DefaultSamplerConfig;
import org.jmxsampler.extensions.base.transformer.regexp.RegExpMappingConfig;
import org.jmxsampler.extensions.base.transformer.regexp.RegExpMetricTransformer;
import org.jmxsampler.extensions.base.writer.console.ConsoleMetricsWriter;
import org.jmxsampler.extensions.base.writer.console.ConsoleWriterConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.service.AbstractLocalObjectFactory;
import org.jmxsampler.transformer.MetricsTransformer;
import org.jmxsampler.writer.MetricsWriter;

public class BaseObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsWriter(final WriterConfig config) {
		return config instanceof ConsoleWriterConfig;
	}

	@Override
	protected MetricsWriter doNewWriter(final WriterConfig config) {
		return new ConsoleMetricsWriter((ConsoleWriterConfig) config);
	}

	@Override
	public boolean supportsTransformer(final MappingConfig config) {
		return config instanceof RegExpMappingConfig;
	}

	@Override
	protected MetricsTransformer doNewTransformer(final MappingConfig config) {
		return new RegExpMetricTransformer((RegExpMappingConfig) config);
	}

	@Override
	public boolean supportsSampler(final SamplerConfig config) {
		return config instanceof DefaultSamplerConfig;
	}

	@Override
	protected Sampler doNewSampler(final SamplerConfig config) {
		final DefaultSamplerConfig actualConfig = (DefaultSamplerConfig) config;
		final MetricsReader reader = getGlobalFactory().newReader(actualConfig.getReader());
		final DefaultSampler result = new DefaultSampler(reader);
		for (final WriterConfig writerConfig : actualConfig.getWriters()) {
			result.addWriter(getGlobalFactory().newWriter(writerConfig));
		}
		for (final MappingConfig mappingConfig : actualConfig.getMappings()) {
			result.addTransformer(getGlobalFactory().newTransformer(mappingConfig));
		}
		return result;
	}

}
