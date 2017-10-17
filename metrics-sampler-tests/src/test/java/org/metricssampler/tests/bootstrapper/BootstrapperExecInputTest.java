package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.exec.ExecInputConfig;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class BootstrapperExecInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("exec/complete.xml");
		
		final ExecInputConfig item = assertSingleInput(config, ExecInputConfig.class);
		assertComplete(item);
	}

	private void assertComplete(final ExecInputConfig item) {
		assertEquals("exec_complete", item.getName());
		assertEquals("command", item.getCommand());
		assertEquals(new File("directory"), item.getDirectory());
		assertSingleStringVariable(item.getVariables(), "string", "value");
		assertEquals(2, item.getArguments().size());
		assertEquals("arg1", item.getArguments().get(0));
		assertEquals("arg2", item.getArguments().get(1));
		assertEquals(2, item.getEnvironment().size());
		assertEquals("one", item.getEnvironment().get("ENV1"));
		assertEquals("two", item.getEnvironment().get("ENV2"));
	}

	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("exec/template.xml");
		
		final ExecInputConfig item = assertInput(config, "exec_complete", ExecInputConfig.class);
		assertComplete(item);
	}
	
	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("exec/minimal.xml");
		
		final ExecInputConfig item = assertSingleInput(config, ExecInputConfig.class);
		assertEquals("exec_minimal", item.getName());
		assertEquals("command", item.getCommand());
	}
}
