package org.metricssampler.tests.bootstrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}