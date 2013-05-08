package org.metricssampler.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameters;

@Parameters(separators="=")
public abstract class AbstractCommand implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final MainCommand mainCommand;

	public AbstractCommand(final MainCommand mainCommand) {
		this.mainCommand = mainCommand;
	}
	
	protected String getConfig() {
		return mainCommand.getConfig();
	}
	
	protected String getControlHost() {
		return mainCommand.getControlHost();
	}
	
	protected int getControlPort() {
		return mainCommand.getControlPort();
	}
}
