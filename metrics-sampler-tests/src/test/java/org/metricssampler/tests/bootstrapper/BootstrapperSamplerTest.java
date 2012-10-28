package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.base.DefaultSamplerConfig;

public class BootstrapperSamplerTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("sampler/complete.xml");
		final DefaultSamplerConfig sampler = assertSampler(config, "jmx-sampler", DefaultSamplerConfig.class);
		assertEquals(1000, sampler.getInterval());
		assertEquals("jmx", sampler.getInput().getName());
		assertEquals("jmx-sampler", sampler.getName());
		assertEquals("console", sampler.getOutputs().get(0).getName());
		assertEquals(2, sampler.getSelectors().size());
		assertEquals(1000, sampler.getInterval());
		assertEquals("pool", sampler.getPool());
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("sampler/minimal.xml");
		final DefaultSamplerConfig sampler = assertSampler(config, "jmx", DefaultSamplerConfig.class);
		assertEquals(1000, sampler.getInterval());
		assertEquals("jmx", sampler.getInput().getName());
		assertEquals("jmx", sampler.getName());
		assertEquals("console1", sampler.getOutputs().get(0).getName());
		assertEquals("console2", sampler.getOutputs().get(1).getName());
		assertEquals(1, sampler.getSelectors().size());
		assertEquals(1000, sampler.getInterval());
		assertEquals("samplers", sampler.getPool());
	}
}
