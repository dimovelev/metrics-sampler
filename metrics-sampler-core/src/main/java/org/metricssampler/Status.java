package org.metricssampler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Status extends ControlRunner {

	public static void main(final String[] args) {
		new Status().run(args);
	}

	@Override
	protected void run(final String host, final int port, final String... args) {
		try {
			final Socket socket = new Socket(host, port);
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.write("status\n");
			writer.flush();
			final String response = reader.readLine();
			if ("ok".equals(response)) {
				System.out.println("Running [port " + port + "]");
				System.exit(0);
			} else {
				System.out.println("Running on control port " + port + " but responded with: " + response);
				System.exit(2);
			}
			writer.close();
			reader.close();
			socket.close();
		} catch (final ConnectException e) {
			System.out.println("Stopped");
			System.exit(0);
		} catch (final IOException e) {
			System.out.println("Unknown state: " + e.getMessage());
			System.exit(1);
		}
	}

}
