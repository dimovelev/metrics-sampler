package org.metricssampler.cmd;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="stop", commandDescriptionKey="help.stop.command")
public class StopCommand extends ControlCommand {
	@Override
	protected void runBootstrapped() {
		shutdown(bootstrapper.getControlHost(), bootstrapper.getControlPort());
	}

	protected String shutdown(final String host, final int port) {
		try {
			final Socket socket = new Socket(host, port);
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("shutdown\n");
			writer.flush();
			closeQuietly(writer);
			closeQuietly(socket);
			return "Stopped";
		} catch (final ConnectException e) {
			return "No daemon running on port " + port;
		} catch (final IOException e) {
			return "Failed to stop: " + e.getMessage();
		}
	}
	
}
