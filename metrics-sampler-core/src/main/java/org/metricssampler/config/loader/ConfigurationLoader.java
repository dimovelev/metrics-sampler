package org.metricssampler.config.loader;

import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.loader.FileGlobProcessor.MatchingFileVisitor;
import org.metricssampler.config.loader.xbeans.ConfigurationXBean;
import org.metricssampler.config.loader.xbeans.IncludeXBean;
import org.metricssampler.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.ReaderWrapper;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

/**
 * Utility class to load a XML configuration file into a {@link ConfigurationXBean} and then convert it to a {@link Configuration}
 */
public class ConfigurationLoader {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Collection<Class<?>> xbeanClasses;
	private final HierarchicalStreamDriver driver = new Xpp3DomDriver();

	public ConfigurationLoader(final Collection<Class<?>> xbeanClasses) {
		checkArgumentNotNullNorEmpty(xbeanClasses, "xbeanClasses");
		this.xbeanClasses = xbeanClasses;
	}

	public static Configuration fromFile(final String filename, final Collection<Class<?>> xbeanClasses) {
		return new ConfigurationLoader(xbeanClasses).load(filename);
	}

	protected Configuration load(final String filename) {
		checkArgumentNotNullNorEmpty(filename, "filename");
		final File file = new File(filename);
		final XStream xstream = createXStream();
		try {
			final ConfigurationXBean result = loadFile(file, xstream);
			return result.toConfig();
		} catch (final XStreamException e) {
			throw new ConfigurationException("Failed to load configuration from \"" + file.getAbsolutePath() + "\"", e);
		} catch (final FileNotFoundException e) {
			throw new ConfigurationException("Failed to load configuration from \"" + file.getAbsolutePath() + "\"", e);
		}
	}

	protected ConfigurationXBean loadFile(final File file, final XStream xstream) throws FileNotFoundException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			final TrimmingReaderWrapper reader = new TrimmingReaderWrapper(driver.createReader(fis));
			final ConfigurationXBean result = (ConfigurationXBean) xstream.unmarshal(reader);
			if (result.getIncludes() != null) {
				for (final IncludeXBean include : result.getIncludes()) {
					logger.info("Including files matching \"{}\"", include.getLocation());
					final File basedir = file.getParentFile();
					include(basedir != null ? basedir : new File("."), include.getLocation(), xstream, result);
				}
			}
			return result;
		} finally {
			IOUtils.closeQuietly(fis);
		}
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

	/**
	 * Trim the attributes and string values
	 */
	private static class TrimmingReaderWrapper extends ReaderWrapper {
		protected TrimmingReaderWrapper(final HierarchicalStreamReader reader) {
			super(reader);
		}

		@Override
		public String getValue() {
			return StringUtils.trim(super.getValue());
		}

		@Override
		public String getAttribute(final String name) {
			return StringUtils.trim(super.getAttribute(name));
		}
		
		
		@Override
		public String getAttribute(final int index) {
			return StringUtils.trim(super.getAttribute(index));
		}
	}
}
