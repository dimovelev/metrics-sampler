package org.jmxsampler.extensions.modqos;

import org.jmxsampler.config.InputConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

public class ModQosObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof ModQosInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		if (config instanceof ModQosInputConfig) {
			return new ModQosMetricsReader((ModQosInputConfig) config);
		} else {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
	}
}
