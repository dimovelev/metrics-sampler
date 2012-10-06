package org.metricssampler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class Stop extends ControlRunner {

	public static void main(final String[] args) {
		new Stop().run(args);
	}

	@Override
	protected void runControl(final String host, final int port, final String... args) {
		shutdown(host, port);
	}

	protected String shutdown(final String host, final int port) {
		try {
			final Socket socket = new Socket(host, port);
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("shutdown\n");
			writer.flush();
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(socket);
			return "Stopped";
		} catch (final ConnectException e) {
			return "No daemon running on port " + port;
		} catch (final IOException e) {
			return "Failed to stop: " + e.getMessage();
		}
	}

}
