package org.metricssampler.extensions.oranosql;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractLocalObjectFactory;

public class OracleNoSQLObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof OracleNoSQLInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new OracleNoSQLMetricsReader((OracleNoSQLInputConfig) config);
	}
}
