package org.metricssampler.service;

import org.metricssampler.config.*;
import org.metricssampler.config.loader.ConfigurationLoader;
import org.metricssampler.config.loader.XBeanPostProcessor;
import org.metricssampler.config.loader.xbeans.*;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.values.ValueTransformer;
import org.metricssampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultBootstrapper implements Bootstrapper {
	public static final List<Class<?>> XBEAN_CLASSES = Arrays.asList(
			ConfigurationXBean.class,
			SelectorGroupXBean.class,
			SelectorGroupRefXBean.class,
			VariableXBean.class,
			StringVariableXBean.class,
			DictionaryVariableXBean.class,
			EntryXBean.class,
			SharedResourceXBean.class,
			SamplerThreadPoolXBean.class,
			HttpConnectionPoolXBean.class
	);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Class<?>> xbeanClasses = new ArrayList<>();
	private final List<LocalObjectFactory> objectFactories = new ArrayList<>();

	private Configuration configuration;
	private List<Sampler> samplers;
	private Map<String, SharedResource> sharedResources;
	private final String controlHost;
	private final int controlPort;
	private List<XBeanPostProcessor> xbeanPostProcessors = new ArrayList<>();
	private final boolean suspended;

	private DefaultBootstrapper(final String controlHost, final int controlPort) {
		ApplicationInfo.initialize();
		this.controlHost = controlHost;
		this.controlPort = controlPort;
		this.suspended = false;
	}

	private DefaultBootstrapper(final boolean suspended) {
		ApplicationInfo.initialize();
		this.controlHost = null;
		this.controlPort = -1;
		this.suspended = suspended;
	}

	public static Bootstrapper bootstrap(final String filename, final boolean suspended) {
		final DefaultBootstrapper result = new DefaultBootstrapper(suspended);
		result.initialize();
		result.loadConfiguration(filename);
		result.createSharedResources();
		result.createSamplers();
		return result;
	}

	public static Bootstrapper bootstrap(final String filename, final String controlHost, final int controlPort) {
		final DefaultBootstrapper result = new DefaultBootstrapper(controlHost, controlPort);
		result.initialize();
		result.loadConfiguration(filename);
		result.createSharedResources();
		result.createSamplers();
		return result;
	}

	public static Bootstrapper bootstrap(final String controlHost, final int controlPort) {
		return new DefaultBootstrapper(controlHost, controlPort);
	}

	private void initialize() {
		xbeanClasses.addAll(XBEAN_CLASSES);

		final ServiceLoader<Extension> services = ServiceLoader.load(Extension.class);
		for (final Extension extension : services) {
			registerExtension(extension);
		}

		Collections.sort(xbeanPostProcessors);
	}

	private void loadConfiguration(final String filename) {
		configuration = ConfigurationLoader.fromFile(filename, xbeanClasses, xbeanPostProcessors);
	}

	private void createSharedResources() {
		logger.debug("Creating shared resources");
		sharedResources = new HashMap<>();
		for (final SharedResourceConfig resourceConfig : configuration.getSharedResources().values()) {
			if (!resourceConfig.isIgnored()) {
				final SharedResource sharedResource = newSharedResource(resourceConfig, suspended);
				sharedResources.put(resourceConfig.getName(), sharedResource);
			}
		}
		logger.debug("Created {} shared resources", sharedResources.size());
	}

	@Override
	public SharedResource newSharedResource(final SharedResourceConfig config, boolean suspended) {
		for (final LocalObjectFactory factory : objectFactories) {
			try {
				if (factory.supportsSharedResource(config)) {
					logger.debug("Creating shared resource {}", config.getName());
					return factory.newSharedResource(config, suspended);
				}
			} catch (final RuntimeException e) {
				throw new ConfigurationException("Failed to create shared resource \"" + config.getName() + "\"", e);
			}
		}
		throw new ConfigurationException("Unsupported shared resource named \"" + config.getName() + "\"");
	}

	private void createSamplers() {
		logger.debug("Creating samplers");
		samplers = new LinkedList<Sampler>();
		for (final SamplerConfig samplerConfig : configuration.getSamplers()) {
			if (!samplerConfig.isIgnored()) {
				final Sampler sampler = newSampler(samplerConfig);
				samplers.add(sampler);
			}
		}
	}

	private void registerExtension(final Extension extension) {
		logger.info("Loading extension {}", extension.getName());
		extension.initialize();
		xbeanClasses.addAll(extension.getXBeans());
		xbeanPostProcessors.addAll(extension.getXBeanPostProcessors());
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
	public ValueTransformer newValueTransformer(final ValueTransformerConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			if (factory.supportsValueTransformer(config)) {
				return factory.newValueTransformer(config);
			}
		}
		throw new ConfigurationException("Unsupported value transformer: " + config);
	}

	@Override
	public Sampler newSampler(final SamplerConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			try {
				if (factory.supportsSampler(config)) {
					return factory.newSampler(config);
				}
			} catch (final RuntimeException e) {
				throw new ConfigurationException("Failed to create sampler \"" + config.getName() + "\"", e);
			}
		}
		throw new ConfigurationException("Unsupported sampler: " + config);
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public Iterable<Sampler> getSamplers() {
		return samplers;
	}

	@Override
	public String getControlHost() {
		return controlHost;
	}

	@Override
	public int getControlPort() {
		return controlPort;
	}

	@Override
	public SharedResource getSharedResource(final String name) {
		final SharedResource result = sharedResources.get(name);
		if (result == null) {
			throw new ConfigurationException("Shared resource \"" + name + "\" not found");
		}
		return result;
	}

	@Override
	public void shutdown() {
		logger.info("Shutting down shared resources");
		for (final SharedResource sharedResource : sharedResources.values()) {
			sharedResource.shutdown();
		}
	}

	@Override
	public Map<String, SharedResource> getSharedResources() {
		return Collections.unmodifiableMap(sharedResources);
	}


}
