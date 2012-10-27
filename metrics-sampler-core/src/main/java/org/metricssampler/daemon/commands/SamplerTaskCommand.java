package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.metricssampler.resources.SamplerTask;

public abstract class SamplerTaskCommand extends BaseControlCommand {
	private final String name;
	private final Map<String, SamplerTask> tasks;

	protected SamplerTaskCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name) {
		super(reader, writer);
		this.tasks = tasks;
		this.name = name;
	}

	protected void executeOnMatchingTasks(final SamplerTaskAction action) throws IOException {
		try {
			final List<SamplerTask> matchingTasks = findMatchingTasks();
			if (matchingTasks.isEmpty()) {
				respond("No samplers found matching regular expression \"" + name + "\"");
			} else {
				for (final SamplerTask task : matchingTasks) {
					action.execute(task, writer);
				}
			}
		} catch (final PatternSyntaxException e) {
			respond("Could not compile sampler name regular expression \"" + name +"\": "+e.getMessage());
		}
	}

	protected static interface SamplerTaskAction {
		void execute(SamplerTask task, BufferedWriter writer) throws IOException;
	}

	protected List<SamplerTask> findMatchingTasks() {
		final List<SamplerTask> result = new LinkedList<SamplerTask>();
		final Pattern nameExpression = Pattern.compile(name);
		for (final Entry<String, SamplerTask> entry : tasks.entrySet()) {
			if (nameExpression.matcher(entry.getKey()).matches()) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
