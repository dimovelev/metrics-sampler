package org.metricssampler.daemon;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.daemon.commands.ControlCommand;
import org.metricssampler.daemon.commands.ControlCommandFactory;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControlCommandParserTest {
	private ControlCommandFactory factory;
	private ControlCommandParser testee;

	@Before
	public void setup() {
		factory = mock(ControlCommandFactory.class);
		testee = new ControlCommandParser(factory);
	}

	@Test
	public void parseShutdown() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.shutdown()).thenReturn(expected);

		final ControlCommand result = testee.parse("shutdown");

		assertSame(expected, result);
	}

	@Test
	public void parseStatus() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.status()).thenReturn(expected);

		final ControlCommand result = testee.parse("status");

		assertSame(expected, result);
	}

	@Test
	public void parseDisableSampler() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.disableSampler("whatever")).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler whatever disable");

		assertSame(expected, result);
	}

	@Test
	public void parseDisableSamplerRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.disableSampler("^what.*ever.+$")).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ disable");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForeverRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForever("^what.*ever.+$")).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForTimesRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForTimes("^what.*ever.+$", 28)).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable for 28 times");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForDurationSecondsRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForDuration("^what.*ever.+$", 28L)).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable for 28 seconds");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForDurationSecondRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForDuration("^what.*ever.+$", 1L)).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable for 1 second");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForDurationMinutesRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForDuration("^what.*ever.+$", 300L)).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable for 5 minutes");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForDurationHourRegExp() {
		final ControlCommand expected = mock(ControlCommand.class);
		when(factory.enableSamplerForDuration("^what.*ever.+$", 3600L)).thenReturn(expected);

		final ControlCommand result = testee.parse("sampler ^what.*ever.+$ enable for 1 hour");

		assertSame(expected, result);
	}

	@Test
	public void parseEnableSamplerForDurationInvalidNumber() {
		final ControlCommand expected = mock(ControlCommand.class);
		final String line = "sampler boo enable for xx minutes";
		when(factory.invalidSyntax(line, "could not parse command")).thenReturn(expected);
		
		final ControlCommand result = testee.parse(line);

		assertSame(expected, result);
	}
	
	@Test
	public void parseEnableSamplerForDurationInvalidUnit() {
		final ControlCommand expected = mock(ControlCommand.class);
		final String line = "sampler boo enable for 10 stunden";
		when(factory.invalidSyntax(line, "could not parse command")).thenReturn(expected);
		
		final ControlCommand result = testee.parse(line);
		
		assertSame(expected, result);
	}

}
