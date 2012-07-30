package org.jmxsampler.extensions.jmx.service;

import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.extensions.jmx.reader.JmxMetricsReader;
import org.jmxsampler.extensions.jmx.reader.JmxReaderConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

public class JmxObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsReader(final ReaderConfig config) {
		return config instanceof JmxReaderConfig;
	}

	@Override
	protected MetricsReader doNewReader(final ReaderConfig config) {
		return new JmxMetricsReader((JmxReaderConfig) config);
	}
}
