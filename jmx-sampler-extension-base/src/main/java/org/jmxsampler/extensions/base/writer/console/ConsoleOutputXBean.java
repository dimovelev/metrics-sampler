package org.jmxsampler.extensions.base.writer.console;

import org.jmxsampler.config.OutputConfig;
import org.jmxsampler.config.loader.xbeans.OutputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("console")
public class ConsoleOutputXBean extends OutputXBean {
	@Override
	public OutputConfig toConfig() {
		validate();
		return new ConsoleOutputConfig(getName());
	}
}
