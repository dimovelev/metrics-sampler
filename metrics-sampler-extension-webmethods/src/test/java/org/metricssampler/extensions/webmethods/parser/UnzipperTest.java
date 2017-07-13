package org.metricssampler.extensions.webmethods.parser;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UnzipperTest {
	private File file;
	private Unzipper testee;

	@Before
	public void setup() {
		file = new File("src/test/resources/diagnostic_data.zip");
		testee = new Unzipper(file, 14000L);
	}

	@After
	public void teardown() throws IOException {
		testee.close();
	}

	@Test
	public void unzip() throws ZipException, IOException {
		final InputStream result = testee.unzip("runtime/JDBCPools.txt");
		assertNotNull(result);
		IOUtils.closeQuietly(result);
	}

	@Test
	public void unzipMissing() throws ZipException, IOException {
		final InputStream result = testee.unzip("runtime/JDBCPools1.txt");
		if (result != null) {
			IOUtils.closeQuietly(result);
		}
		assertNull(result);
	}

	@Test
	public void unzipTooLarge() throws ZipException, IOException {
		final InputStream result = testee.unzip("runtime/ThreadList.txt");
		if (result != null) {
			IOUtils.closeQuietly(result);
		}
		assertNull(result);
	}
}
