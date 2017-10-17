package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.extensions.base.DefaultSamplerConfig;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(60, sampler.getInitialResetTimeout());
        assertEquals(600, sampler.getRegularResetTimeout());
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("sampler/minimal.xml");
		final DefaultSamplerConfig sampler = assertSampler(config, "jmx", DefaultSamplerConfig.class);
		assertEquals(1000, sampler.getInterval());
		assertEquals("jmx", sampler.getInput().getName());
		assertEquals("jmx", sampler.getName());
        Set<String> outputNames = new HashSet<>();
        for (OutputConfig item  : sampler.getOutputs()) {
            outputNames.add(item.getName());
        }

        assertTrue("Outputs should contain console1", outputNames.contains("console1"));
        assertTrue("Outputs should contain console2", outputNames.contains("console2"));
		assertEquals(1, sampler.getSelectors().size());
		assertEquals(1000, sampler.getInterval());
		assertEquals("samplers", sampler.getPool());
        assertEquals(-1, sampler.getInitialResetTimeout());
        assertEquals(-1, sampler.getRegularResetTimeout());
	}
}
