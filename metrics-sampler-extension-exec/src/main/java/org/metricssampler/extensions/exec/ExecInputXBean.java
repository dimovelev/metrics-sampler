package org.metricssampler.extensions.exec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.InputXBean;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

@XStreamAlias("exec")
public class ExecInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String command;
	
	@XStreamAsAttribute
	private String directory;
	
	private List<ArgumentXBean> arguments;
	
	private List<EntryXBean> environment;
	
	public String getCommand() {
		return command;
	}

	public void setCommand(final String command) {
		this.command = command;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(final String directory) {
		this.directory = directory;
	}

	public List<ArgumentXBean> getArguments() {
		return arguments;
	}

	public void setArguments(final List<ArgumentXBean> arguments) {
		this.arguments = arguments;
	}

	public List<EntryXBean> getEnvironment() {
		return environment;
	}

	public void setEnvironment(final List<EntryXBean> environment) {
		this.environment = environment;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "command", getName());
		if (arguments != null && !arguments.isEmpty()) {
			for (final ArgumentXBean argument : arguments) {
				argument.validate();
			}
		}
		if (environment != null && !environment.isEmpty()) {
			for (final EntryXBean entry : environment) {
				entry.validate();
			}
		}
	}

	@Override
	protected ExecInputConfig createConfig() {
		return new ExecInputConfig(getName(), getVariablesConfig(), directory != null ? new File(directory) : null, command, getArgumentsConfig(), getEnvironmentConfig());
	}

	protected List<String> getArgumentsConfig() {
		final List<String> result = new LinkedList<>();
		if (arguments != null) {
			for (final ArgumentXBean argument : arguments) {
				result.add(argument.getValue());
			}
		}
		return result;
	}

	private Map<String, String> getEnvironmentConfig() {
		final Map<String, String> result = new HashMap<>();
		if (environment != null) {
			for (final EntryXBean entry : environment) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

}
