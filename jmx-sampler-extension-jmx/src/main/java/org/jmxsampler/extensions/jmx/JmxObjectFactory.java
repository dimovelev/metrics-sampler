package org.jmxsampler.extensions.jmx;

import org.jmxsampler.config.InputConfig;
import org.jmxsampler.reader.MetricsReader;
import org.jmxsampler.service.AbstractLocalObjectFactory;

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
