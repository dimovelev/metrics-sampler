package org.metricssampler.cmd;

import java.util.ResourceBundle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="help", commandDescriptionKey="help.help.command")
public class HelpCommand extends AbstractCommand {
	private final JCommander commander;
	private final ResourceBundle bundle;
	@Parameter(names="-c", descriptionKey="help.param.command")
	private String command;
	
	public HelpCommand(final JCommander commander, final ResourceBundle bundle) {
		this.commander = commander;
		this.bundle = bundle;
	}

	@Override
	public void run() {
		usage(null, false);
	}

	public void error(final String key) {
		usage(bundle.getString(key), true);
	}

	public void usage(final String msg, final boolean error) {
		System.out.println("metrics-sampler ver. " + bundle.getString("help.version"));
		if (msg != null) {
			if (error) {
				System.err.println(msg);
			} else {
				System.out.println(msg);
			}
		}
		if (command != null) {
			commander.usage(command);
		} else {
			commander.usage();
		}
	}
	
	public void error(final ParameterException e) {
		usage(e.getMessage(), true);
	}
}
