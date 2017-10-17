package org.metricssampler.daemon;

import org.metricssampler.daemon.commands.ControlCommand;
import org.metricssampler.daemon.commands.ControlCommandFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControlCommandParser {
	private static final String CMD_SHUTDOWN = "shutdown";
	private static final String CMD_STATUS = "status";

	private static final Pattern PATTERN_SAMPLER_ACTION = Pattern.compile("^sampler (.+) (enable|disable|reset|list)$");
	private static final Pattern PATTERN_SAMPLER_ENABLE_FOR_TIMES = Pattern.compile("^sampler (.+) enable for ([0-9]+) times$");
	private static final Pattern PATTERN_SAMPLER_ENABLE_FOR_DURATION = Pattern.compile("^sampler (.+) enable for ([0-9]+) (hour|minute|second)s?$");

	private static final Pattern PATTERN_RESOURCE_ACTION = Pattern.compile("^resource (.+) (start|stop)$");

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
			if (line.startsWith("sampler ")) {
				final ControlCommand result = processSamplerCommand(line);
				if (result != null) {
					return result;
				}
			} else if (line.startsWith("resource ")) {
				final ControlCommand result = processResourceCommand(line);
				if (result != null) {
					return result;
				}
			}
			return factory.invalidSyntax(line, "could not parse command");
		}

	}

	protected ControlCommand processSamplerCommand(final String line) {
		final Matcher actionMatcher = PATTERN_SAMPLER_ACTION.matcher(line);
		if (actionMatcher.matches()) {
			final String name = actionMatcher.group(1);
			final String action = actionMatcher.group(2);
			if ("enable".equals(action)) {
				return factory.enableSamplerForever(name);
			} else if ("disable".equals(action)) {
				return factory.disableSampler(name);
			} else if ("reset".equals(action)) {
				return factory.resetSampler(name);
			} else if ("list".equals(action)) {
				System.out.println("LISTING");
				return factory.listSampler(name);
			} else {
				return factory.invalidSyntax(line, "Unsupported action \"" + action + "\"");
			}
		}
		final Matcher enableForTimesMatcher = PATTERN_SAMPLER_ENABLE_FOR_TIMES.matcher(line);
		if (enableForTimesMatcher.matches()) {
			final String name = enableForTimesMatcher.group(1);
			try {
				final int times = Integer.parseInt(enableForTimesMatcher.group(2));
				return factory.enableSamplerForTimes(name, times);
			} catch (final NumberFormatException e) {
				return factory.invalidSyntax(line, "could not parse repetitions as an integer number");
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
		return null;
	}

	protected ControlCommand processResourceCommand(final String line) {
		final Matcher actionMatcher = PATTERN_RESOURCE_ACTION.matcher(line);
		if (actionMatcher.matches()) {
			final String name = actionMatcher.group(1);
			final String action = actionMatcher.group(2);
			if ("start".equals(action)) {
				return factory.startResource(name);
			} else if ("stop".equals(action)) {
				return factory.stopResource(name);
			} else {
				return factory.invalidSyntax(line, "Unsupported action \"" + action + "\"");
			}
		}
		return null;
	}
}
