package org.metricssampler.service;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.loader.ConfigurationLoader;
import org.metricssampler.config.loader.xbeans.ConfigurationXBean;
import org.metricssampler.config.loader.xbeans.DictionaryPlaceholderXBean;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.PlaceholderXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupRefXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupXBean;
import org.metricssampler.config.loader.xbeans.StringPlaceholderXBean;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsRegistry implements GlobalObjectFactory {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Class<?>> xbeanClasses = new LinkedList<Class<?>>();
	private final List<LocalObjectFactory> objectFactories = new LinkedList<LocalObjectFactory>();

	public ExtensionsRegistry() {
		addDefaultXBeanClasses();
		
		final ServiceLoader<Extension> services = ServiceLoader.load(Extension.class);
		for (final Extension extension : services) {
			registerExtension(extension);
		}
	}

	protected void addDefaultXBeanClasses() {
		xbeanClasses.add(ConfigurationXBean.class);
		xbeanClasses.add(SelectorGroupXBean.class);
		xbeanClasses.add(SelectorGroupRefXBean.class);
		xbeanClasses.add(PlaceholderXBean.class);		
		xbeanClasses.add(StringPlaceholderXBean.class);		
		xbeanClasses.add(DictionaryPlaceholderXBean.class);		
		xbeanClasses.add(EntryXBean.class);
	}

	private void registerExtension(final Extension extension) {
		logger.info("Loading extension " + extension.getName());
		xbeanClasses.addAll(extension.getXBeans());
		final LocalObjectFactory localObjectFactory = extension.getObjectFactory();
		localObjectFactory.setGlobalFactory(this);
		objectFactories.add(localObjectFactory);
	}

	@Override
	public MetricsReader newReaderForInput(final InputConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsInput(config)) {
				return factory.newReaderForInput(config);
			}
		}
		throw new IllegalArgumentException("Unsupported input: " + config);
	}

	@Override
	public MetricsWriter newWriterForOutput(final OutputConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsOutput(config)) {
				return factory.newWriterForOutput(config);
			}
		}
		throw new ConfigurationException("Unsupported output: " + config);
	}

	@Override
	public MetricsSelector newSelector(final SelectorConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsSelector(config)) {
				return factory.newSelector(config);
			}
		}
		throw new ConfigurationException("Unsupported selector: " + config);
	}

	@Override
	public Sampler newSampler(final SamplerConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsSampler(config)) {
				return factory.newSampler(config);
			}
		}
		throw new ConfigurationException("Unsupported sampler: " + config);
	}

	public ConfigurationLoader newConfigurationLoader() {
		return new ConfigurationLoader(xbeanClasses);
	}
}
