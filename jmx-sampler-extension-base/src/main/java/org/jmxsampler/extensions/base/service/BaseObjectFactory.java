package org.jmxsampler.extensions.base.service;

import org.jmxsampler.config.SelectorConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.extensions.base.sampler.DefaultSampler;
import org.jmxsampler.extensions.base.sampler.DefaultSamplerConfig;
import org.jmxsampler.extensions.base.selector.regexp.RegExpMetricsSelector;
import org.jmxsampler.extensions.base.selector.regexp.RegExpSelectorConfig;
import org.jmxsampler.extensions.base.writer.console.ConsoleMetricsWriter;
import org.jmxsampler.extensions.base.writer.console.ConsoleOutputConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.selector.MetricsSelector;
import org.jmxsampler.service.AbstractLocalObjectFactory;
import org.jmxsampler.writer.MetricsWriter;

public class BaseObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsOutput(final OutputConfig config) {
		return config instanceof ConsoleOutputConfig;
	}

	@Override
	protected MetricsWriter doNewWriter(final OutputConfig config) {
		return new ConsoleMetricsWriter((ConsoleOutputConfig) config);
	}

	@Override
	public boolean supportsSelector(final SelectorConfig config) {
		return config instanceof RegExpSelectorConfig;
	}

	@Override
	protected MetricsSelector doNewSelector(final SelectorConfig config) {
		return new RegExpMetricsSelector((RegExpSelectorConfig) config);
	}

	@Override
	public boolean supportsSampler(final SamplerConfig config) {
		return config instanceof DefaultSamplerConfig;
	}

	@Override
	protected Sampler doNewSampler(final SamplerConfig config) {
		final DefaultSamplerConfig actualConfig = (DefaultSamplerConfig) config;
		final MetricsReader reader = getGlobalFactory().newReaderForInput(actualConfig.getReader());
		final DefaultSampler result = new DefaultSampler(reader, actualConfig.getPlaceholders());
		for (final OutputConfig writerConfig : actualConfig.getOutputs()) {
			result.addWriter(getGlobalFactory().newWriterForOutput(writerConfig));
		}
		for (final SelectorConfig selector : actualConfig.getSelectors()) {
			result.addSelector(getGlobalFactory().newSelector(selector));
		}
		return result;
	}

}
