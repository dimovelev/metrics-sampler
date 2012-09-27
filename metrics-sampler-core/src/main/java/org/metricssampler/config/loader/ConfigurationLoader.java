package org.metricssampler.config.loader;

import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

import java.io.File;
import java.util.Collection;

import org.metricssampler.config.Configuration;
import org.metricssampler.config.loader.FileGlobProcessor.MatchingFileVisitor;
import org.metricssampler.config.loader.xbeans.ConfigurationXBean;
import org.metricssampler.config.loader.xbeans.IncludeXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigurationLoader {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Collection<Class<?>> xbeanClasses;

	public ConfigurationLoader(final Collection<Class<?>> xbeanClasses) {
		checkArgumentNotNullNorEmpty(xbeanClasses, "xbeanClasses");
		this.xbeanClasses = xbeanClasses;
	}

	public Configuration load(final String filename) {
		checkArgumentNotNullNorEmpty(filename, "filename");
		final XStream xstream = createXStream();
		final ConfigurationXBean result = loadFile(filename, xstream);
		return result.toConfig();
	}

	protected ConfigurationXBean loadFile(final String filename, final XStream xstream) {
		final File file = new File(filename);
		final ConfigurationXBean result = (ConfigurationXBean) xstream.fromXML(file);
		if (result.getIncludes() != null) {
			for (final IncludeXBean include : result.getIncludes()) {
				logger.info("Including files matching {}", include.getLocation());
				final File basedir = file.getParentFile();
				include(basedir, include.getLocation(), xstream, result);
			}
		}
		return result;
	}

	protected void include(final File basedir, final String location, final XStream xstream, final ConfigurationXBean result) {
		FileGlobProcessor.visitMatching(basedir, location, new MatchingFileVisitor() {
			@Override
			public void visit(final File file) {
				logger.info("Including file {}", file);
				final ConfigurationXBean includedConfig = (ConfigurationXBean) xstream.fromXML(file);
				result.include(includedConfig);
			}
		});
	}
	
	protected XStream createXStream() {
		final XStream result = new XStream(new DomDriver());
		for (final Class<?> clazz : xbeanClasses) {
			result.processAnnotations(clazz);
		}
		return result;
	}
}
