package org.metricssampler.cmd;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.TCPControllerTestUtil;

public class StopTest {
	private StopCommand testee;
	
	@Before
	public void setup() {
		testee = new StopCommand(new MainCommand());
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
