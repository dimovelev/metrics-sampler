package org.metricssampler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
						closeQuietly(reader);
						closeQuietly(writer);
						closeQuietly(socket);
						closeQuietly(serverSocket);
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
