package org.metricssampler.daemon;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.metricssampler.daemon.commands.ControlCommand;
import org.metricssampler.daemon.commands.ControlCommandFactory;

public class ControlCommandParser {
	private static final String CMD_SHUTDOWN = "shutdown";
	private static final String CMD_STATUS = "status";

	private static final Pattern PATTERN_SAMPLER_DISABLE = Pattern.compile("^sampler (.+) disable$");
	private static final Pattern PATTERN_SAMPLER_ENABLE = Pattern.compile("^sampler (.+) enable$");
	private static final Pattern PATTERN_SAMPLER_ENABLE_FOR_TIMES = Pattern.compile("^sampler (.+) enable for ([0-9]+) times$");
	private static final Pattern PATTERN_SAMPLER_ENABLE_FOR_DURATION = Pattern.compile("^sampler (.+) enable for ([0-9]+) (hour|minute|second)s?$");

	private final ControlCommandFactory factory;

	public ControlCommandParser(final ControlCommandFactory factory) {
		this.factory = factory;
	}

	public ControlCommand parse(final String line) {
		if (line == null) {
			return null;
		}
		if (line.equals(CMD_SHUTDOWN)) {
			return factory.shutdown();
		} else if (line.equals(CMD_STATUS)) {
			return factory.status();
		} else {
			final Matcher disableMatcher = PATTERN_SAMPLER_DISABLE.matcher(line);
			if (disableMatcher.matches()) {
				final String name = disableMatcher.group(1);
				return factory.disableSampler(name);
			}
			final Matcher enableMatcher = PATTERN_SAMPLER_ENABLE.matcher(line);
			if (enableMatcher.matches()) {
				final String name = enableMatcher.group(1);
				return factory.enableSamplerForever(name);
			}
			final Matcher enableForTimesMatcher = PATTERN_SAMPLER_ENABLE_FOR_TIMES.matcher(line);
			if (enableForTimesMatcher.matches()) {
				final String name = enableForTimesMatcher.group(1);
				try {
					final int times = Integer.parseInt(enableForTimesMatcher.group(2));
					return factory.enableSamplerForTimes(name, times);
				} catch (final NumberFormatException e) {
					return factory.invalidSyntax(line, "could not interpret repetitions as an integer number");
				}
			}
			final Matcher enableForDurationMatcher = PATTERN_SAMPLER_ENABLE_FOR_DURATION.matcher(line);
			if (enableForDurationMatcher.matches()) {
				final String name = enableForDurationMatcher.group(1);
				try {
					final int duration = Integer.parseInt(enableForDurationMatcher.group(2));
					final String unitAsString = (enableForDurationMatcher.group(3) + "s").toUpperCase();
					try {
						final TimeUnit unit = TimeUnit.valueOf(unitAsString);
						final long seconds = unit.toSeconds(duration);
						return factory.enableSamplerForDuration(name, seconds);
					} catch (final IllegalArgumentException e) {
						return factory.invalidSyntax(line, "time unit \"" + unitAsString + "\" is not supported");
					}
				} catch (final NumberFormatException e) {
					return factory.invalidSyntax(line, "could not interpret repetitions as an integer number");
				}
			}
			return factory.invalidSyntax(line, "could not parse command");
		}

	}
}
