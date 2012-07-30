package org.jmxsampler.config.loader;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.jmxsampler.config.Configuration;
import org.jmxsampler.config.loader.xbeans.ConfigurationXBean;
import org.jmxsampler.config.loader.xbeans.MappingTemplateRefXBean;
import org.jmxsampler.config.loader.xbeans.MappingTemplateXBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigurationLoader {
	private final Collection<Class<?>> xbeanClasses = new LinkedList<Class<?>>();

	public ConfigurationLoader(final Collection<Class<?>> xbeanClasses) {
		this.xbeanClasses.add(ConfigurationXBean.class);
		this.xbeanClasses.add(MappingTemplateXBean.class);
		this.xbeanClasses.add(MappingTemplateRefXBean.class);
		this.xbeanClasses.addAll(xbeanClasses);
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
