package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.ThreadPoolConfig;
import org.metricssampler.extensions.base.ConsoleOutputConfig;
import org.metricssampler.extensions.base.DefaultSamplerConfig;
import org.metricssampler.extensions.jmx.JmxInputConfig;

import static org.junit.Assert.assertTrue;

public class BootstrapperIncludeTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("include/root.xml");
		assertInput(config, "jmx", JmxInputConfig.class);
		assertInput(config, "jmx.01", JmxInputConfig.class);

		assertOutput(config, "console", ConsoleOutputConfig.class);
		assertOutput(config, "jmx.console", ConsoleOutputConfig.class);
		
		assertSampler(config, "jmx", DefaultSamplerConfig.class);
		assertSampler(config, "jmx.01", DefaultSamplerConfig.class);
		
		assertSharedResource(config, "samplers", ThreadPoolConfig.class);
		assertSharedResource(config, "jmx.samplers", ThreadPoolConfig.class);
		
		assertTrue(config.getVariables().containsKey("prefix"));
		assertTrue(config.getVariables().containsKey("jmx.prefix"));
	}
}
