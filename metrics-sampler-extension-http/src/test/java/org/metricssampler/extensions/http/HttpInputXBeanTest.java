package org.metricssampler.extensions.http;

import org.junit.Test;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.extensions.http.parsers.regexp.RegExpHttpResponseParserXBean;
import org.metricssampler.extensions.http.parsers.regexp.RegExpLineFormatXBean;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class HttpInputXBeanTest {

	@Test(expected = ConfigurationException.class)
	public void toConfigNoName() {
		final HttpInputXBean testee = new HttpInputXBean();
		testee.toConfig();
	}

	@Test(expected = ConfigurationException.class)
	public void toConfigNoUrl() {
		final HttpInputXBean testee = new HttpInputXBean();
		testee.setName("name");
		testee.toConfig();
	}

	@Test
	public void toConfigNoAuth() {
		final HttpInputXBean testee = new HttpInputXBean();
		testee.setName("name");
		testee.setUrl("http://localhost");
		testee.setParser(Arrays.asList(createValidParser()));

		final HttpInputConfig result = (HttpInputConfig) testee.toConfig();

		assertEquals("name", result.getName());
		assertEquals("http://localhost", result.getUrl().toExternalForm());
	}

	protected HttpResponseParserXBean createValidParser() {
		final RegExpHttpResponseParserXBean result = new RegExpHttpResponseParserXBean();
		final RegExpLineFormatXBean regexp = new RegExpLineFormatXBean();
		regexp.setExpression("(.+)=(.+)");
		regexp.setNameIndex(1);
		regexp.setValueIndex(2);
		result.setExpressions(Arrays.asList(regexp));
		return result;
	}

	@Test
	public void toConfigWithLoginAndHeaders() {
		final HttpInputXBean testee = new HttpInputXBean();
		final EntryXBean header1 = new EntryXBean();
		header1.setKey("header1");
		header1.setValue("value1");
		testee.setHeaders(Arrays.asList(header1));
		testee.setName("name");
		testee.setUrl("http://localhost");
		testee.setUsername("user");
		testee.setPassword("pass");
		testee.setParser(Arrays.asList(createValidParser()));

		final HttpInputConfig result = (HttpInputConfig) testee.toConfig();

		assertEquals("user", result.getUsername());
		assertEquals("pass", result.getPassword());
		assertEquals(1, result.getHeaders().size());
		assertEquals("value1", result.getHeaders().get("header1"));
	}

	@Test(expected = ConfigurationException.class)
	public void toConfigNoParser() {
		final HttpInputXBean testee = new HttpInputXBean();
		testee.setName("name");
		testee.setUrl("http://localhost");

		testee.toConfig();
	}

}
