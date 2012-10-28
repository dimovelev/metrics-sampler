package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.metricssampler.config.Configuration;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SharedResourceConfig;
import org.metricssampler.config.SocketOptionsConfig;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.DefaultBootstrapper;

public abstract class BootstrapperTestBase {
	@Before
	public void setup() {
		System.setProperty("control.port", "28111");
	}

	protected String getConfig(final String path) {
		return "src/test/configs/bootstrapper/" + path;
	}
	
	protected Bootstrapper bootstrap(final String filename) {
		return DefaultBootstrapper.bootstrap(getConfig(filename));
	}
	
	protected Configuration configure(final String filename) {
		final Bootstrapper bootstrapper = bootstrap(filename);
		assertNotNull(bootstrapper);
		final Configuration result = bootstrapper.getConfiguration();
		assertNotNull(result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends OutputConfig> T assertSingleOutput(final Configuration config, final Class<T> clazz) {
		assertEquals(1, config.getOutputs().size());
		final OutputConfig result = config.getOutputs().iterator().next();
		assertNotNull(result);
		assertEquals(clazz, result.getClass());
		return (T) result;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends SharedResourceConfig> T assertSingleSharedResource(final Configuration config, final Class<T> clazz) {
		assertEquals(1, config.getSharedResources().size());
		final SharedResourceConfig result = config.getSharedResources().values().iterator().next();
		assertNotNull(result);
		assertEquals(clazz, result.getClass());
		return (T) result;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends InputConfig> T assertSingleInput(final Configuration config, final Class<T> clazz) {
		assertEquals(1, config.getInputs().size());
		final InputConfig result = config.getInputs().iterator().next();
		assertNotNull(result);
		assertEquals(clazz, result.getClass());
		return (T) result;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends InputConfig> T assertInput(final Configuration config, final String name, final Class<T> clazz) {
		for (final InputConfig result : config.getInputs()) {
			if (result.getName().equals(name)) {
				assertEquals(clazz, result.getClass());
				return (T) result;
			}
		}
		fail("Could not find input named " + name);
		return null;
	}

	protected void assertSocketOptions(final SocketOptionsConfig so, final int connectTimeout, final int soTimeout, final int sndBuffSize, final int rcvBuffSize) {
		assertNotNull(so);
		assertEquals(connectTimeout, so.getConnectTimeout());
		assertEquals(soTimeout, so.getSoTimeout());
		assertEquals(sndBuffSize, so.getSndBuffSize());
		assertEquals(rcvBuffSize, so.getRcvBuffSize());
	}
	
	protected void assertSingleStringVariable(final Map<String, Object> variables, final String name, final String value) {
		assertNotNull(variables);
		assertEquals(1, variables.size());
		assertEquals(value, variables.get(name));
	}
	
	protected void assertSingleEntry(final Map<String, String> entries, final String key, final String value) {
		assertNotNull(entries);
		assertEquals(1, entries.size());
		assertEquals(value, entries.get(key));
	}
}