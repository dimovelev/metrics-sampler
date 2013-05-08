package org.metricssampler;

import org.metricssampler.cmd.CheckCommand;
import org.metricssampler.cmd.MainCommand;
import org.metricssampler.cmd.MetadataCommand;
import org.metricssampler.cmd.StartCommand;
import org.metricssampler.cmd.StatusCommand;
import org.metricssampler.cmd.StopCommand;
import org.metricssampler.cmd.TestCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Runner {
	public static void main(final String[] args) {
		final MainCommand mainCommand = new MainCommand();
		final CheckCommand checkCommand = new CheckCommand(mainCommand);
		final StartCommand startCommand = new StartCommand(mainCommand);
		final StopCommand stopCommand = new StopCommand(mainCommand);
		final StatusCommand statusCommand = new StatusCommand(mainCommand);
		final TestCommand testCommand = new TestCommand(mainCommand);
		final MetadataCommand metadataCommand = new MetadataCommand(mainCommand);

		final JCommander jc = new JCommander(mainCommand);
		jc.setAcceptUnknownOptions(false);
		jc.setCaseSensitiveOptions(false);
		jc.setProgramName("metrics-sampler");
		jc.addCommand(checkCommand);
		jc.addCommand(startCommand);
		jc.addCommand(stopCommand);
		jc.addCommand(statusCommand);
		jc.addCommand(testCommand);
		jc.addCommand(metadataCommand);
		try {
			jc.parse(args);
		} catch (final ParameterException e) {
			System.err.println(e.getMessage());
			if (jc.getParsedCommand() == null) {
				jc.usage();
			} else {
				jc.usage(jc.getParsedCommand());
			}
			System.exit(1);
		}
		if (mainCommand.isHelp()) {
			if (jc.getParsedCommand() != null) {
				jc.usage(jc.getParsedCommand());
			} else {
				jc.usage();
			}
			System.exit(0);
		}
		if (jc.getParsedCommand() != null) {
			final JCommander parsedCommander = jc.getCommands().get(jc.getParsedCommand());
			final Runnable cmd = (Runnable) parsedCommander.getObjects().get(0);
			cmd.run();
		} else {
			System.err.println("Please specify a command");
			jc.usage();
			System.exit(2);
		}
	}
}
