package org.metricssampler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Stop extends ControlCommand {

	public static void main(final String[] args) {
		new Stop().process(args);
	}

	@Override
	protected void process(final String host, final int port, final String... args) {
		try {
			final Socket socket = new Socket(host, port);
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("shutdown\n");
			writer.close();
			socket.close();
		} catch (final ConnectException e) {
			System.out.println("No daemon running on port " + port);
			System.exit(0);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
