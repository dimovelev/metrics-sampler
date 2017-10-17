package org.metricssampler.extensions.webmethods.parser;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.extensions.webmethods.WebMethodsInputXBean;
import org.metricssampler.reader.Metrics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.zip.ZipException;

public abstract class ParserTestBase {

	private WebMethodsInputConfig config;
	private Unzipper unzipper;

	public ParserTestBase() {
		super();
	}

	protected WebMethodsInputConfig getConfig() {
		return config;
	}

	protected Unzipper getUnzipper() {
		return unzipper;
	}

	@Before
	public void setup() throws ZipException, IOException {
		final File file = new File("src/test/resources/diagnostic_data.zip");
		config = new WebMethodsInputConfig("whatever", new HashMap<String, Object>(), new URL("file://dummy"), "user", "pass", new HashMap<String, String>(), true, null, Long.MAX_VALUE, new SimpleDateFormat(WebMethodsInputXBean.DEFAULT_DATE_FORMAT));
		unzipper = new Unzipper(file, getConfig().getMaxEntrySize());
	}

	@After
	public void tearDown() throws IOException {
		unzipper.close();
	}

	protected Metrics doParse() throws ZipException, IOException, ParseException {
		final AbstractFileParser testee = createTestee();
		for (final String name : unzipper.getEntries()) {
			if (testee.canParseEntry(name)) {
				final InputStream stream = unzipper.unzip(name);
				try {
					return testee.parse(stream);
				} finally {
					IOUtils.closeQuietly(stream);
				}
			}
		}
		throw new ParseException("Could not find any parseable entry", 0);
	}

	protected abstract AbstractFileParser createTestee();

}