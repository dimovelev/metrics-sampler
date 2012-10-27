package org.metricssampler.extensions.base.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.ThreadPoolConfig;
import org.metricssampler.extensions.base.resources.DefaultSamplerThreadPool;
import org.metricssampler.extensions.base.sampler.DefaultSampler;
import org.metricssampler.extensions.base.sampler.DefaultSamplerConfig;
import org.metricssampler.extensions.base.sampler.DefaultSamplerXBean;
import org.metricssampler.extensions.base.selector.regexp.RegExpMetricsSelector;
import org.metricssampler.extensions.base.selector.regexp.RegExpSelectorConfig;
import org.metricssampler.extensions.base.selector.regexp.RegExpSelectorXBean;
import org.metricssampler.extensions.base.writer.console.ConsoleMetricsWriter;
import org.metricssampler.extensions.base.writer.console.ConsoleOutputConfig;
import org.metricssampler.extensions.base.writer.console.ConsoleOutputXBean;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.service.AbstractExtension;
import org.metricssampler.writer.MetricsWriter;

public class BaseExtension extends AbstractExtension {

	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ConsoleOutputXBean.class);
		result.add(RegExpSelectorXBean.class);
		result.add(DefaultSamplerXBean.class);
		return result;
	}
	
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
		final DefaultSampler result = new DefaultSampler(actualConfig, reader);
		for (final OutputConfig writerConfig : actualConfig.getOutputs()) {
			result.addWriter(getGlobalFactory().newWriterForOutput(writerConfig));
		}
		for (final SelectorConfig selector : actualConfig.getSelectors()) {
			result.addSelector(getGlobalFactory().newSelector(selector));
		}
		return result;
	}

	@Override
	protected SharedResource doNewSharedResource(final SharedResourceConfig config) {
		final ThreadPoolConfig actualConfig = (ThreadPoolConfig) config;
		return new DefaultSamplerThreadPool(actualConfig);
	}

	@Override
	public boolean supportsSharedResource(final SharedResourceConfig config) {
		return config instanceof ThreadPoolConfig;
	}
	
	
}
