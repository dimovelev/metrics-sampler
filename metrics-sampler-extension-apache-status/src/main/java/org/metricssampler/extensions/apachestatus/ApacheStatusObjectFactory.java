package org.metricssampler.extensions.apachestatus;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractLocalObjectFactory;

public class ApacheStatusObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof ApacheStatusInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		if (config instanceof ApacheStatusInputConfig) {
			return new ApacheStatusMetricsReader((ApacheStatusInputConfig) config);
		} else {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
	}
}
