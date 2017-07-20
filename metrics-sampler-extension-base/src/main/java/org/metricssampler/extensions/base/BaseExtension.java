package org.metricssampler.extensions.base;

import org.metricssampler.config.*;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.service.AbstractExtension;
import org.metricssampler.writer.MetricsWriter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BaseExtension extends AbstractExtension {
	private ELFactory elFactory;
	
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ConsoleOutputXBean.class);
		result.add(RegExpSelectorXBean.class);
		result.add(DefaultSamplerXBean.class);
		result.add(SelfInputXBean.class);
		result.add(ELValueTransformerXBean.class);
		return result;
	}
	
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof SelfInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new SelfMetricsReader((SelfInputConfig) config);
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
		final MetricsReader reader = getGlobalFactory().newReaderForInput(actualConfig.getInput());
		final DefaultSampler result = new DefaultSampler(actualConfig, reader);
		for (final OutputConfig writerConfig : actualConfig.getOutputs()) {
			result.addWriter(getGlobalFactory().newWriterForOutput(writerConfig));
		}
		for (final SelectorConfig selector : actualConfig.getSelectors()) {
			result.addSelector(getGlobalFactory().newSelector(selector));
		}
		for (final ValueTransformerConfig item : actualConfig.getValueTransformers()) {
			result.addValueTransformer(getGlobalFactory().newValueTransformer(item));
		}
		return result;
	}

	@Override
	protected SharedResource doNewSharedResource(final SharedResourceConfig config, boolean suspended) {
		final ThreadPoolConfig actualConfig = (ThreadPoolConfig) config;
		return new DefaultSamplerThreadPool(actualConfig, suspended);
	}

	@Override
	public boolean supportsSharedResource(final SharedResourceConfig config) {
		return config instanceof ThreadPoolConfig;
	}

	@Override
	public boolean supportsValueTransformer(final ValueTransformerConfig config) {
		return config instanceof ELValueTransformerConfig;
	}

	@Override
	protected ELValueTransformer doNewValueTransformer(final ValueTransformerConfig config) {
		final ELValueTransformerConfig actualConfig = (ELValueTransformerConfig) config;
		return new ELValueTransformer(actualConfig, elFactory);
	}

	@Override
	public void initialize() {
		elFactory = new ELFactory();
	}
}
