package org.jmxsampler.extensions.modqos;

import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

public class ModQosObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsReader(final ReaderConfig config) {
		return config instanceof ModQosReaderConfig;
	}

	@Override
	protected MetricsReader doNewReader(final ReaderConfig config) {
		if (config instanceof ModQosReaderConfig) {
			return new ModQosMetricsReader((ModQosReaderConfig) config);
		} else {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
	}
}
