package org.metricssampler.extensions.exec;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExecExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<Class<?>>();
		result.add(ExecInputXBean.class);
		result.add(ArgumentXBean.class);
		return result;
	}
	
	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof ExecInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		return new ExecMetricsReader((ExecInputConfig) config);
	}
}
