package org.jmxsampler.extensions.jdbc;

import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

public class JdbcObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsReader(final ReaderConfig config) {
		return config instanceof JdbcReaderConfig;
	}

	@Override
	protected MetricsReader doNewReader(final ReaderConfig config) {
		return new JdbcMetricsReader((JdbcReaderConfig) config);
	}
}
