package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class MainCommand {
	@Parameter(names = "--help", help=true)
	private boolean help;
	
	@Parameter(names="--config", description="Configuration file", validateWith=FileExistingValidator.class)
	private String config = "config/config.xml";
	
	@Parameter(names="--control-host", description="The host on which the control endpoint is bound.")
	private String controlHost = "localhost";

	@Parameter(names="--control-port", description="The port on which the control endpoint is bound.")
	private int controlPort = 28111;

	public void setControlHost(final String controlHost) {
		this.controlHost = controlHost;
	}

	public void setControlPort(final int controlPort) {
		this.controlPort = controlPort;
	}

	public String getConfig() {
		return config;
	}

	public boolean isHelp() {
		return help;
	}

	public String getControlHost() {
		return controlHost;
	}

	public int getControlPort() {
//		return Integer.parseInt(controlPort);
		return controlPort;
	}
}
