package org.metricssampler.extensions.base;

import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.loader.xbeans.OutputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("console")
public class ConsoleOutputXBean extends OutputXBean {
	@Override
	public OutputConfig toConfig() {
		validate();
		return new ConsoleOutputConfig(getName());
	}
}
