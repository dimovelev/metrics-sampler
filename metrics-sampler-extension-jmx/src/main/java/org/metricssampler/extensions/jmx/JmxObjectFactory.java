package org.metricssampler.extensions.jmx;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractLocalObjectFactory;

public class JmxObjectFactory extends AbstractLocalObjectFactory {
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof JmxInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new JmxMetricsReader((JmxInputConfig) config);
	}
}
