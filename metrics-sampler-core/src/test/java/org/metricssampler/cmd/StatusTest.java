package org.metricssampler.cmd;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.TCPControllerTestUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StatusTest {
	private StatusCommand testee;

	@Before
	public void setup() throws IOException {
		testee = new StatusCommand();
	}

	@Test
	public void checkStatusRunning() {
		final int port = TCPControllerTestUtil.setupServer("status", "ok");

		final String response = testee.checkStatus("localhost", port);

		assertEquals("Running [port " + port + "]", response);
	}

	@Test
	public void checkStatusRunningStrangeResponse() {
		final int port = TCPControllerTestUtil.setupServer("status", "mostly ok");

		final String response = testee.checkStatus("localhost", port);

		assertEquals("Running on control port " + port + " but responded with: \"mostly ok\"", response);
	}

	@Test
	public void checkStatusNotRunning() {
		final String response = testee.checkStatus("localhost", 28311);

		assertEquals("Stopped", response);
	}

}
