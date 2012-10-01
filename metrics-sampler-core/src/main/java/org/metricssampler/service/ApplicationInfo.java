package org.metricssampler.service;

import static org.metricssampler.util.Preconditions.checkStateNotNull;
import static org.metricssampler.util.Preconditions.checkStateNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.metricssampler.config.ConfigurationException;

public class ApplicationInfo {
	private static final String PROPERTIES_FILE_NAME = "application.info";
	private static ApplicationInfo instance;
	
	private final String version;

	protected static void initialize() {
		checkStateNull(instance, "instance");
		final InputStream propertiesStream = ApplicationInfo.class.getResourceAsStream("application.properties");
		if (propertiesStream == null) {
			throw new ConfigurationException("Could not load " + PROPERTIES_FILE_NAME + " from classpath");
		}
		try {
			final Properties properties = new Properties();
			properties.load(propertiesStream);
			final String version = properties.getProperty("version");
			if (version == null) {
				throw new ConfigurationException("Could not find version property in " + PROPERTIES_FILE_NAME);
			}
			instance = new ApplicationInfo(version); 
		} catch (final IOException e) {
			throw new ConfigurationException("Could not load " + PROPERTIES_FILE_NAME + " from classpath", e);
		} finally {
			IOUtils.closeQuietly(propertiesStream);
		}
	}
	
	public static ApplicationInfo getInstance() {
		checkStateNotNull(instance, "instance");
		return instance;
	}

	private ApplicationInfo(final String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
