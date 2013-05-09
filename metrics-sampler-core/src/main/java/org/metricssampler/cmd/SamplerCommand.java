package org.metricssampler.cmd;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="sampler", commandDescriptionKey="help.sampler.command")
public class SamplerCommand extends ControlCommand {
	@Parameter(names = "-s", descriptionKey="help.param.sampler.samplers", required=true)
	protected List<String> samplers = new LinkedList<String>();

	@Parameter(names = "-a", descriptionKey="help.param.sampler.action", required=true)
	protected String action;

	@Override
	protected void runBootstrapped() {
		for (final String sampler : samplers) {
			final String prefix = action + " sampler \"" + sampler + "\": ";
			try {
				final String response = execute(bootstrapper.getControlHost(), bootstrapper.getControlPort(), "sampler " + sampler + " " + action);
				System.out.println(prefix + response);
			} catch (final UnknownHostException e) {
				System.err.println(prefix + e.getMessage());
			} catch (final IOException e) {
				System.err.println(prefix + e.getMessage());
			}
		}
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
