package org.metricssampler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class Status extends ControlRunner {

	public static void main(final String[] args) {
		new Status().run(args);
	}

	@Override
	protected void runControl(final String host, final int port, final String... args) {
		final String msg = checkStatus(host, port);
		System.out.println(msg);
		System.exit(msg.startsWith("Running [") ? 0 : 1);
	}

	protected String checkStatus(final String host, final int port) {
		Socket socket = null;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			socket = new Socket(host, port);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.write("status\n");
			writer.flush();
			final String response = reader.readLine();
			if ("ok".equals(response)) {
				return "Running [port " + port + "]";
			} else {
				return "Running on control port " + port + " but responded with: " + response;
			}
		} catch (final ConnectException e) {
			return "Stopped";
		} catch (final IOException e) {
			return "Unknown state: " + e.getMessage();
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(socket);
		}
	}

}
