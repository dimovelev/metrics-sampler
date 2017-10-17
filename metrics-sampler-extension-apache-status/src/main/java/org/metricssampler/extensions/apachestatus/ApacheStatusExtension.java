package org.metricssampler.extensions.apachestatus;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ApacheStatusExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ApacheStatusInputXBean.class);
		return result;
	}
	
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
