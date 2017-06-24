package org.metricssampler.extensions.oranosql;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OracleNoSQLExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(OracleNoSQLInputXBean.class);
		return result;
	}
	
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof OracleNoSQLInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		final OracleNoSQLInputConfig typedConfig = (OracleNoSQLInputConfig) config;
		if (typedConfig.isAuthenticationRequired()) {
			return new AuthenticatedOracleNoSQLMetricsReader(typedConfig);
		} else {
			return new AnonymousOracleNoSQLMetricsReader(typedConfig);
		}
	}

}
