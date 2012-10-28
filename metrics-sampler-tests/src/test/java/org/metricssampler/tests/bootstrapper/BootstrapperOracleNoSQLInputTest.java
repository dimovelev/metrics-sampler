package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;

public class BootstrapperOracleNoSQLInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("oracle-nosql/complete.xml");
		
		final OracleNoSQLInputConfig item = assertSingleInput(config, OracleNoSQLInputConfig.class);
		assertComplete(item);
	}

	private void assertComplete(final OracleNoSQLInputConfig item) {
		assertEquals("nosql", item.getName());
		assertEquals(2, item.getHosts().size());
		final Iterator<HostConfig> iterator = item.getHosts().iterator();
		final HostConfig host1 = iterator.next();
		assertEquals("host1", host1.getHost());
		assertEquals(2811, host1.getPort());
		final HostConfig host2 = iterator.next();
		assertEquals("host2", host2.getHost());
		assertEquals(2812, host2.getPort());
		assertSingleStringVariable(item.getVariables(), "string", "value");
	}

	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("oracle-nosql/template.xml");
		
		final OracleNoSQLInputConfig item = assertInput(config, "nosql", OracleNoSQLInputConfig.class);
		assertComplete(item);
	}
	
	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("oracle-nosql/minimal.xml");
		
		final OracleNoSQLInputConfig item = assertSingleInput(config, OracleNoSQLInputConfig.class);
		assertEquals("nosql", item.getName());
		assertEquals(1, item.getHosts().size());
		final HostConfig host1 = item.getHosts().iterator().next();
		assertEquals("host1", host1.getHost());
		assertEquals(2811, host1.getPort());
		assertTrue(item.getVariables().isEmpty());
	}
}
