package org.metricssampler.daemon.commands;

import java.io.IOException;

public interface ControlCommand {
	void execute() throws IOException;
}