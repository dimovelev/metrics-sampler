package org.metricssampler.config.loader;

import java.io.File;
import java.util.Collection;

import org.metricssampler.config.Configuration;
import org.metricssampler.config.loader.xbeans.ConfigurationXBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigurationLoader {
	private final Collection<Class<?>> xbeanClasses;

	public ConfigurationLoader(final Collection<Class<?>> xbeanClasses) {
		this.xbeanClasses = xbeanClasses;
	}

	public Configuration load(final String filename) {
		final XStream xstream = createXStream();
		final ConfigurationXBean result = (ConfigurationXBean) xstream.fromXML(new File(filename));
		return result.toConfig();
	}

	protected XStream createXStream() {
		final XStream result = new XStream(new DomDriver());
		for (final Class<?> clazz : xbeanClasses) {
			result.processAnnotations(clazz);
		}
		return result;
	}
}
