package org.metricssampler.extensions.base.service;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.extensions.base.sampler.DefaultSampler;
import org.metricssampler.extensions.base.sampler.DefaultSamplerConfig;
import org.metricssampler.extensions.base.selector.regexp.RegExpMetricsSelector;
import org.metricssampler.extensions.base.selector.regexp.RegExpSelectorConfig;
import org.metricssampler.extensions.base.writer.console.ConsoleMetricsWriter;
import org.metricssampler.extensions.base.writer.console.ConsoleOutputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.service.AbstractLocalObjectFactory;
import org.metricssampler.writer.MetricsWriter;

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
		final DefaultSampler result = new DefaultSampler(actualConfig, reader, actualConfig.getVariables());
		for (final OutputConfig writerConfig : actualConfig.getOutputs()) {
			result.addWriter(getGlobalFactory().newWriterForOutput(writerConfig));
		}
		for (final SelectorConfig selector : actualConfig.getSelectors()) {
			result.addSelector(getGlobalFactory().newSelector(selector));
		}
		return result;
	}

}
