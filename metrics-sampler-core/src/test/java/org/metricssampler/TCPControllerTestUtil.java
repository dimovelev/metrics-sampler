package org.metricssampler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class TCPControllerTestUtil {
	public static int setupServer(final String request, final String response) {
		try {
			final ServerSocket serverSocket = new ServerSocket(0);
			final Runnable server = new Runnable() {
				@Override
				public void run() {
					Socket socket = null;
					BufferedReader reader = null;
					BufferedWriter writer = null;
					try {
						socket = serverSocket.accept();
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						final String line = reader.readLine();
						assertEquals(request, line);
						writer.write(response + "\n");
						writer.flush();
					} catch (final IOException e) {
						fail("Failed to server response: " + e.getMessage());
					} finally {
						IOUtils.closeQuietly(reader);
						IOUtils.closeQuietly(writer);
						IOUtils.closeQuietly(socket);
						IOUtils.closeQuietly(serverSocket);
					}
				}
			};
			new Thread(server).start();
			return serverSocket.getLocalPort();
		} catch (final IOException e) {
			fail("Failed to setup server socket: " + e.getMessage());
		}
		return -1;
	}

}
