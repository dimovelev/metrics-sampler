package org.metricssampler.extensions.apachestatus;

import org.junit.Test;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.loader.xbeans.EntryXBean;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ApacheStatusInputXBeanTest {

	@Test(expected = ConfigurationException.class)
	public void toConfigNoName() {
		final ApacheStatusInputXBean testee = new ApacheStatusInputXBean();
		testee.toConfig();
	}
	
	@Test(expected = ConfigurationException.class)
	public void toConfigNoUrl() {
		final ApacheStatusInputXBean testee = new ApacheStatusInputXBean();
		testee.setName("name");
		testee.toConfig();
	}

	@Test
	public void toConfigNoAuth() {
		final ApacheStatusInputXBean testee = new ApacheStatusInputXBean();
		testee.setName("name");
		testee.setUrl("http://localhost");
		
		final ApacheStatusInputConfig result = (ApacheStatusInputConfig) testee.toConfig();
		
		assertEquals("name", result.getName());
		assertEquals("http://localhost", result.getUrl().toExternalForm());
	}
	
	@Test
	public void toConfigWithLoginAndHeaders() {
		final ApacheStatusInputXBean testee = new ApacheStatusInputXBean();
		final EntryXBean header1 = new EntryXBean();
		header1.setKey("header1");
		header1.setValue("value1");
		testee.setHeaders(Arrays.asList(header1));
		testee.setName("name");
		testee.setUrl("http://localhost");
		testee.setUsername("user");
		testee.setPassword("pass");
		
		final ApacheStatusInputConfig result = (ApacheStatusInputConfig) testee.toConfig();
		
		assertEquals("user", result.getUsername());
		assertEquals("pass", result.getPassword());
		assertEquals(1, result.getHeaders().size());
		assertEquals("value1", result.getHeaders().get("header1"));
	}

}
