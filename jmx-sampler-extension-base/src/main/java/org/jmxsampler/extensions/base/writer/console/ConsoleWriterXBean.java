package org.jmxsampler.extensions.base.writer.console;

import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.config.loader.xbeans.WriterXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("console-writer")
public class ConsoleWriterXBean extends WriterXBean {
	@Override
	public WriterConfig toConfig() {
		validate();
		return new ConsoleWriterConfig(getName());
	}
}
