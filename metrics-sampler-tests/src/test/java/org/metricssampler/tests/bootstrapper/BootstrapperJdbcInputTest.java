package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.metricssampler.config.Configuration;
import org.metricssampler.extensions.jdbc.JdbcConnectionPoolConfig;
import org.metricssampler.extensions.jdbc.JdbcInputConfig;
import org.metricssampler.service.Bootstrapper;

public class BootstrapperJdbcInputTest extends BootstrapperTestBase {
	@Test
	public void bootstrapComplete() {
		final Bootstrapper result = bootstrap("jdbc/complete.xml");
		
		final Configuration config = result.getConfiguration();
		assertNotNull(config);
		final JdbcInputConfig item = assertSingleInput(config, JdbcInputConfig.class);
		assertEquals("jdbc", item.getName());
		assertEquals("pool", item.getPool());
		assertEquals(2, item.getQueries().size());
		assertTrue(item.getQueries().contains("select 'first' from dual"));
		assertTrue(item.getQueries().contains("select 'second' from dual"));
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
	public void bootstrapMinimal() {
		final Bootstrapper result = bootstrap("jdbc/minimal.xml");
		
		final Configuration config = result.getConfiguration();
		assertNotNull(config);
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
