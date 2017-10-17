package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.ThreadPoolConfig;

import static org.junit.Assert.assertEquals;

public class BootstrapperThreadPoolSharedResourceTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("thread-pool/complete.xml");
		
		final ThreadPoolConfig threadpool = assertSingleSharedResource(config, ThreadPoolConfig.class);
		assertEquals("samplers", threadpool.getName());
		assertEquals(10, threadpool.getCoreSize());
		assertEquals(20, threadpool.getMaxSize());
		assertEquals(60, threadpool.getKeepAliveTime());
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapMissingName() {
		configure("thread-pool/missing-name.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapMissingSize() {
		configure("thread-pool/missing-size.xml");
	}
	
	@Test(expected=ConfigurationException.class)
	public void bootstrapDuplicateName() {
		configure("thread-pool/duplicate-name.xml");
	}

}
