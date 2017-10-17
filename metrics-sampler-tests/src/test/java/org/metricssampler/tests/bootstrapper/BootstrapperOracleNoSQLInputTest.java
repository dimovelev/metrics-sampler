package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;

import java.util.Iterator;

import static org.junit.Assert.*;

public class BootstrapperOracleNoSQLInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapAnonymous() {
		final Configuration anonymousConfig = configure("oracle-nosql/anonymous.xml");
		
		final OracleNoSQLInputConfig item = assertSingleInput(anonymousConfig, OracleNoSQLInputConfig.class);
		assertAnonymous(item);
	}

	private void assertAnonymous(final OracleNoSQLInputConfig item) {
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
		assertFalse(item.isAuthenticationRequired());
	}

	@Test
	public void bootstrapAuthenticated() {
		final Configuration anonymousConfig = configure("oracle-nosql/authenticated.xml");

		final OracleNoSQLInputConfig item = assertSingleInput(anonymousConfig, OracleNoSQLInputConfig.class);
		assertAuthenticated(item);
	}

	private void assertAuthenticated(final OracleNoSQLInputConfig item) {
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
		assertTrue(item.isAuthenticationRequired());
		assertEquals("test", item.getStoreName());
		assertEquals("user", item.getLogin().getUsername());
		assertEquals("pass", item.getLogin().getPassword());
		assertEquals("pom.xml", item.getTrustFile().getFileName().toString());
	}

	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("oracle-nosql/template.xml");
		
		final OracleNoSQLInputConfig item = assertInput(config, "nosql", OracleNoSQLInputConfig.class);
		assertAnonymous(item);
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
