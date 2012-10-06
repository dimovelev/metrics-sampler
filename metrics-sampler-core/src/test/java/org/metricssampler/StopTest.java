package org.metricssampler;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class StopTest {
	private Stop testee;
	
	@Before
	public void setup() {
		testee = new Stop();
	}
	
	@Test
	public void shutdownOk() {
		final int port = TCPControllerTestUtil.setupServer("shutdown", "ok");

		final String response = testee.shutdown("localhost", port);
		
		assertEquals("Stopped", response);
	}
	
	@Test
	public void shutdownNotRunning() {
		final String response = testee.shutdown("localhost", 12832);
		
		assertEquals("No daemon running on port 12832", response);
	}

}
