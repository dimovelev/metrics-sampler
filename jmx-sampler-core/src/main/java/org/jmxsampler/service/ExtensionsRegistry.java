package org.jmxsampler.service;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.config.loader.ConfigurationLoader;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.sampler.Sampler;
import org.jmxsampler.transformer.MetricsTransformer;
import org.jmxsampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsRegistry implements GlobalObjectFactory {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Class<?>> xbeanClasses = new LinkedList<Class<?>>();
	private final List<LocalObjectFactory> objectFactories = new LinkedList<LocalObjectFactory>();

	public ExtensionsRegistry() {
		final ServiceLoader<Extension> services = ServiceLoader.load(Extension.class);
		for (final Extension extension : services) {
			registerExtension(extension);
		}
	}

	private void registerExtension(final Extension extension) {
		logger.info("Loading extension " + extension.getName());
		xbeanClasses.addAll(extension.getXBeans());
		final LocalObjectFactory localObjectFactory = extension.getObjectFactory();
		localObjectFactory.setGlobalFactory(this);
		objectFactories.add(localObjectFactory);
	}

	@Override
	public MetricsReader newReader(final ReaderConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsReader(config)) {
				return factory.newReader(config);
			}
		}
		throw new IllegalArgumentException("Unsupported reader config: " + config);
	}

	@Override
	public MetricsWriter newWriter(final WriterConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsWriter(config)) {
				return factory.newWriter(config);
			}
		}
		throw new IllegalArgumentException("Unsupported writer config: " + config);
	}

	@Override
	public MetricsTransformer newTransformer(final MappingConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsTransformer(config)) {
				return factory.newTransformer(config);
			}
		}
		throw new IllegalArgumentException("Unsupported mapping config: " + config);
	}

	@Override
	public Sampler newSampler(final SamplerConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsSampler(config)) {
				return factory.newSampler(config);
			}
		}
		throw new IllegalArgumentException("Unsupported sampler config: " + config);
	}

	public ConfigurationLoader newConfigurationLoader() {
		return new ConfigurationLoader(xbeanClasses);
	}
}
