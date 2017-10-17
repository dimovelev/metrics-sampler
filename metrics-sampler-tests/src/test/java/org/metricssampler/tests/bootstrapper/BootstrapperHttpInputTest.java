package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.http.HttpInputConfig;
import org.metricssampler.extensions.http.parsers.regexp.RegExpHttpResponseParser;
import org.metricssampler.extensions.http.parsers.regexp.RegExpLineFormat;

import static org.junit.Assert.*;

public class BootstrapperHttpInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("http/complete.xml");

		final HttpInputConfig item = assertInput(config, "http", HttpInputConfig.class);
		assertEquals("http", item.getName());
		assertEquals("username", item.getUsername());
		assertEquals("password", item.getPassword());
		assertEquals("http://localhost", item.getUrl().toExternalForm());
		assertFalse(item.isPreemptiveAuthEnabled());
		assertSingleStringVariable(item.getVariables(), "string", "value");
		assertSingleEntry(item.getHeaders(), "header", "val");
		assertNotNull(item.getParser());
		assertTrue(item.getParser() instanceof RegExpHttpResponseParser);
		final RegExpHttpResponseParser parser = (RegExpHttpResponseParser) item.getParser();
		assertEquals(2, parser.getLineFormats().size());
		final RegExpLineFormat format1 = parser.getLineFormats().get(0);
		assertNotNull(format1.getPattern());
		assertEquals(1, format1.getNameGroupIndex());
		assertEquals(2, format1.getValueGroupIndex());
		assertNotNull(item.getSocketOptions());
		assertEquals(5, item.getSocketOptions().getConnectTimeout());
		assertEquals(10, item.getSocketOptions().getSoTimeout());
		assertTrue(item.getSocketOptions().isKeepAlive());
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("http/minimal.xml");

		final HttpInputConfig item = assertSingleInput(config, HttpInputConfig.class);
		assertEquals("http", item.getName());
		assertNull(item.getUsername());
		assertNull(item.getPassword());
		assertEquals("http://localhost", item.getUrl().toExternalForm());
		assertTrue(item.isPreemptiveAuthEnabled());
		assertTrue(item.getVariables().isEmpty());
		assertTrue(item.getHeaders().isEmpty());
		assertNotNull(item.getParser());
	}
}
