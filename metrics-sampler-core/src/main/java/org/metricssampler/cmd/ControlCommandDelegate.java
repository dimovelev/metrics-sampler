package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;

public class ControlCommandDelegate {
	@Parameter(names="-h", descriptionKey="help.param.control.host")
	private String host = "localhost";

	@Parameter(names="-p", descriptionKey="help.param.control.port")
	private int port = 28111;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
