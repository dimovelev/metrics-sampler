package org.metricssampler.extensions.exec;

import org.metricssampler.config.InputConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public class ExecInputConfig extends InputConfig {
	private final File directory;
	private final String command;
	private final List<String> arguments;
	private final Map<String, String> environment;

	public ExecInputConfig(final String name, final Map<String, Object> variables, final File directory, final String command,
			final List<String> arguments, final Map<String, String> environment) {
		super(name, variables);
		checkArgumentNotNullNorEmpty(command, "command");
		checkArgumentNotNull(arguments, "arguments");
		checkArgumentNotNull(environment, "environment");
		this.directory = directory;
		this.command = command;
		this.arguments = arguments;
		this.environment = environment;
	}

	/**
	 * @return the directory where the process should be executed (its working directory). Leave {@code null} for the current working
	 *         directory.
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * @return The command to execute. Cannot be {@code null} or an empty string.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return A list of command line arguments to pass to the command. Cannot be {@code null} but can be an empty list
	 */
	public List<String> getArguments() {
		return arguments;
	}

	/**
	 * @return A map representing the environment variables to add to the current environment and use as environment of the new process.
	 *         Cannot be {@code null} but can be an empty map.
	 */
	public Map<String, String> getEnvironment() {
		return environment;
	}
}
