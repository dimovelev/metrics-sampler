package org.metricssampler.extensions.jmx;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

public class JmxExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(JmxInputXBean.class);
		result.add(IgnoreObjectNameXBean.class);
		return result;
	}
	
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof JmxInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new JmxMetricsReader((JmxInputConfig) config);
	}
}
