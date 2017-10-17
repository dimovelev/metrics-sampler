package org.metricssampler.extensions.base;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.loader.xbeans.OutputXBean;

@XStreamAlias("console")
public class ConsoleOutputXBean extends OutputXBean {
	@Override
	public OutputConfig toConfig() {
		validate();
		return new ConsoleOutputConfig(getName(), isDefault());
	}
}
