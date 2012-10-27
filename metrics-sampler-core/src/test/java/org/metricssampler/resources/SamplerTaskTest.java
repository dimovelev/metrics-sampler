package org.metricssampler.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.sampler.Sampler;

public class SamplerTaskTest {
	private Sampler sampler;
	private SamplerConfig config;
	private SamplerTask testee;
	
	@Before
	public void setup() {
		sampler = mock(Sampler.class);
		config = mock(SamplerConfig.class);
		when(sampler.getConfig()).thenReturn(config);
		when(config.isDisabled()).thenReturn(false);
		testee = new SamplerTask(sampler);
	}

	@Test
	public void run() {
		testee.run();
		verify(sampler, times(1)).sample();
	}
	
	@Test
	public void runDisableEnable() {
		testee.disable();
		testee.run();
		verify(sampler, never()).sample();
		testee.enable();
		testee.run();
		verify(sampler).sample();
	}
	
	@Test
	public void runEnableForTimes() {
		testee.disable();
		testee.run();
		verify(sampler, never()).sample();
		testee.enableForTimes(2);
		testee.run();
		testee.run();
		testee.run();
		testee.run();
		verify(sampler, times(2)).sample();
	}
	
	@Test
	public void runEnableForDuration() {
		testee.disable();
		testee.run();
		verify(sampler, never()).sample();
		when(config.getInterval()).thenReturn(2);
		testee.enableForDuration(7);
		testee.run();
		testee.run();
		testee.run();
		testee.run();
		testee.run();
		verify(sampler, times(3)).sample();
	}
	
	@Test
	public void runRuntimeException() {
		doThrow(RuntimeException.class).when(sampler).sample();
		testee.run();
		testee.run();
		verify(sampler, times(2)).sample();
	}
	
	@Test
	public void runDisable() {
		when(config.isDisabled()).thenReturn(true);
		testee = new SamplerTask(sampler);
		testee.run();
		verify(sampler, never()).sample();
	}

}
