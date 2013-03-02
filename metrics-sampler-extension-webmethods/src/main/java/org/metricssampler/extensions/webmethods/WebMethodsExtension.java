package org.metricssampler.extensions.webmethods;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

public class WebMethodsExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(WebMethodsInputXBean.class);
		return result;
	}
	
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof WebMethodsInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		if (config instanceof WebMethodsInputConfig) {
			return new WebMethodsMetricsReader((WebMethodsInputConfig) config);
		} else {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
	}
}
