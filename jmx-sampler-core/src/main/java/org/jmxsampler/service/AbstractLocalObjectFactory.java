package org.jmxsampler.service;

import org.jmxsampler.config.InputConfig;
import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.SelectorConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.selector.MetricsSelector;
import org.jmxsampler.writer.MetricsWriter;

/**
 * Base implementation intended for subclassing by extensions
 */
public abstract class AbstractLocalObjectFactory implements LocalObjectFactory {
	private GlobalObjectFactory globalFactory;

	@Override
	public void setGlobalFactory(final GlobalObjectFactory globalFactory) {
		this.globalFactory = globalFactory;
	}

	protected GlobalObjectFactory getGlobalFactory() {
		return globalFactory;
	}

	@Override
	public boolean supportsOutput(final OutputConfig config) {
		return false;
	}

	@Override
	public final MetricsWriter newWriterForOutput(final OutputConfig config) {
		if (!supportsOutput(config)) {
			throw new IllegalArgumentException("Unsupported writer config: " + config);
		}
		return doNewWriter(config);
	}

	protected MetricsWriter doNewWriter(final OutputConfig config) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsInput(final InputConfig config) {
		return false;
	}

	@Override
	public final MetricsReader newReaderForInput(final InputConfig config) {
		if (!supportsInput(config)) {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
		return doNewReader(config);
	}

	protected MetricsReader doNewReader(final InputConfig config) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsSelector(final SelectorConfig config) {
		return false;
	}

	@Override
	public final MetricsSelector newSelector(final SelectorConfig config) {
		if (!supportsSelector(config)) {
			throw new IllegalArgumentException("Unsupported selector: " + config);
		}
		return doNewSelector(config);
	}

	protected MetricsSelector doNewSelector(final SelectorConfig config) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsSampler(final SamplerConfig config) {
		return false;
	}

	@Override
	public final Sampler newSampler(final SamplerConfig config) {
		if (!supportsSampler(config)) {
			throw new IllegalArgumentException("Unsupported sampler config: " + config);
		}
		return doNewSampler(config);
	}

	protected Sampler doNewSampler(final SamplerConfig config) {
		throw new UnsupportedOperationException();
	}


}
