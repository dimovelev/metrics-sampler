package org.jmxsampler.extensions.jdbc;

import org.jmxsampler.config.InputConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

public class JdbcObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof JdbcInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new JdbcMetricsReader((JdbcInputConfig) config);
	}
}
