package org.metricssampler.cmd;



public abstract class AbstractCommand implements Runnable {
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
