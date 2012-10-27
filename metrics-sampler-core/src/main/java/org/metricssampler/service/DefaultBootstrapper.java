package org.metricssampler.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.loader.ConfigurationLoader;
import org.metricssampler.config.loader.xbeans.ConfigurationXBean;
import org.metricssampler.config.loader.xbeans.DictionaryVariableXBean;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupRefXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupXBean;
import org.metricssampler.config.loader.xbeans.SharedResourceXBean;
import org.metricssampler.config.loader.xbeans.StringVariableXBean;
import org.metricssampler.config.loader.xbeans.SamplerThreadPoolXBean;
import org.metricssampler.config.loader.xbeans.VariableXBean;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.selector.MetricsSelector;
import org.metricssampler.writer.MetricsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBootstrapper implements Bootstrapper {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Class<?>> xbeanClasses = new LinkedList<Class<?>>();
	private final List<LocalObjectFactory> objectFactories = new LinkedList<LocalObjectFactory>();

	private Configuration configuration;
	private List<Sampler> samplers;
	private Map<String, SharedResource> sharedResources;
	private String controlHost;
	private int controlPort;

	private DefaultBootstrapper() {
		ApplicationInfo.initialize();
	}
	
	public static Bootstrapper bootstrap(final String filename) {
		final DefaultBootstrapper result = new DefaultBootstrapper();
		result.initialize();
		result.loadConfiguration(filename);
		result.createSharedResources();
		result.createSamplers();
		return result;
	}

	public static Bootstrapper bootstrap() {
		final DefaultBootstrapper result = new DefaultBootstrapper();
		result.loadFromEnvironment();
		return result;
	}

	private void initialize() {
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
		xbeanClasses.add(VariableXBean.class);		
		xbeanClasses.add(StringVariableXBean.class);		
		xbeanClasses.add(DictionaryVariableXBean.class);		
		xbeanClasses.add(EntryXBean.class);
		xbeanClasses.add(SharedResourceXBean.class);
		xbeanClasses.add(SamplerThreadPoolXBean.class);
	}

	private void loadConfiguration(final String filename) {
		loadFromEnvironment();
		final ConfigurationLoader loader = new ConfigurationLoader(xbeanClasses);
		configuration = loader.load(filename);
	}

	private void loadFromEnvironment() {
		controlHost = System.getProperty("control.host", "localhost");
		controlPort = 0;
		try {
			controlPort = Integer.parseInt(System.getProperty("control.port", "undefined"));
		} catch (final NumberFormatException e) {
			throw new ConfigurationException("Please provide a valid control port using -Dcontrol.port");
		}
	}
	
	private void createSharedResources() {
		logger.debug("Creating shared resources");
		sharedResources = new HashMap<String, SharedResource>();
		for (final SharedResourceConfig resourceConfig : configuration.getSharedResources().values()) {
			if (!resourceConfig.isIgnored()) {
				final SharedResource sharedResource = newSharedResource(resourceConfig);
				sharedResources.put(resourceConfig.getName(), sharedResource);
			}
		}
		logger.debug("Created {} shared resources", sharedResources.size());
	}

	@Override
	public SharedResource newSharedResource(final SharedResourceConfig config) {
		for (final LocalObjectFactory factory : objectFactories) {
			try {
				if (factory.supportsSharedResource(config)) {
					logger.debug("Creating shared resource {}", config.getName());
					return factory.newSharedResource(config);
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
		return sharedResources.get(name);
	}

	@Override
	public void shutdown() {
		logger.info("Shutting down shared resources");
		for (final SharedResource sharedResource : sharedResources.values()) {
			sharedResource.shutdown();
		}
	}
}
