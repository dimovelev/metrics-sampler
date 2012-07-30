package org.jmxsampler.service;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.transformer.MetricsTransformer;
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
	public boolean supportsWriter(final WriterConfig config) {
		return false;
	}

	@Override
	public final MetricsWriter newWriter(final WriterConfig config) {
		if (!supportsWriter(config)) {
			throw new IllegalArgumentException("Unsupported writer config: " + config);
		}
		return doNewWriter(config);
	}

	protected MetricsWriter doNewWriter(final WriterConfig config) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsReader(final ReaderConfig config) {
		return false;
	}

	@Override
	public final MetricsReader newReader(final ReaderConfig config) {
		if (!supportsReader(config)) {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
		return doNewReader(config);
	}

	protected MetricsReader doNewReader(final ReaderConfig config) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsTransformer(final MappingConfig config) {
		return false;
	}

	@Override
	public final MetricsTransformer newTransformer(final MappingConfig config) {
		if (!supportsTransformer(config)) {
			throw new IllegalArgumentException("Unsupported mapping config: " + config);
		}
		return doNewTransformer(config);
	}

	protected MetricsTransformer doNewTransformer(final MappingConfig config) {
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
