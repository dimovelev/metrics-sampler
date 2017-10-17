package org.metricssampler.tests.bootstrapper;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.jdbc.JdbcConnectionPoolConfig;
import org.metricssampler.extensions.jdbc.JdbcInputConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BootstrapperJdbcInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Configuration config = configure("jdbc/complete.xml");
		
		final JdbcInputConfig item = assertSingleInput(config, JdbcInputConfig.class);
		assertComplete(config, item);
	}

	private void assertComplete(final Configuration config, final JdbcInputConfig item) {
		assertEquals("jdbc", item.getName());
		assertEquals("pool", item.getPool());
		assertEquals(2, item.getQueries().size());
		assertTrue(item.getQueries().contains("select 'one', 123 from dual"));
		assertTrue(item.getQueries().contains("select 'two', 232, 12312312311 from dual"));
		assertSingleStringVariable(item.getVariables(), "string", "value");
		
		final JdbcConnectionPoolConfig pool = assertSingleSharedResource(config, JdbcConnectionPoolConfig.class);
		assertEquals("pool", pool.getName());
		assertEquals("username", pool.getUsername());
		assertEquals("password", pool.getPassword());
		assertEquals("url", pool.getUrl());
		assertEquals("driver", pool.getDriver());
		assertEquals(10, pool.getMinSize());
		assertEquals(20, pool.getMaxSize());
		assertEquals(3, pool.getLoginTimeout());
	}

	@Test
	public void bootstrapTemplate() {
		final Configuration config = configure("jdbc/template.xml");
		
		final JdbcInputConfig item = assertInput(config, "jdbc", JdbcInputConfig.class);
		assertComplete(config, item);
	}

	@Test
	public void bootstrapMinimal() {
		final Configuration config = configure("jdbc/minimal.xml");

		final JdbcInputConfig item = assertSingleInput(config, JdbcInputConfig.class);
		assertEquals("jdbc", item.getName());
		assertEquals("pool", item.getPool());
		assertEquals(1, item.getQueries().size());
		assertTrue(item.getQueries().contains("select 'first' from dual"));
		assertTrue(item.getVariables().isEmpty());
		
		final JdbcConnectionPoolConfig pool = assertSingleSharedResource(config, JdbcConnectionPoolConfig.class);
		assertEquals("pool", pool.getName());
		assertEquals("username", pool.getUsername());
		assertEquals("password", pool.getPassword());
		assertEquals("url", pool.getUrl());
		assertEquals("driver", pool.getDriver());
		assertEquals(10, pool.getMinSize());
		assertEquals(20, pool.getMaxSize());
		assertEquals(5, pool.getLoginTimeout());
	}
}
