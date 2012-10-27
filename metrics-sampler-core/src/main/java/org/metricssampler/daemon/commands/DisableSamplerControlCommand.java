package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.metricssampler.resources.SamplerTask;

public class DisableSamplerControlCommand extends SamplerTaskCommand {
	protected DisableSamplerControlCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name) {
		super(reader, writer, tasks, name);
	}

	@Override
	public void execute() throws IOException {
		executeOnMatchingTasks(new SamplerTaskAction() {
			@Override
			public void execute(final SamplerTask task, final BufferedWriter writer) throws IOException {
				logger.info("Disabling sampler \"{}\"", task.getName());
				task.disable();
				respond("Sampler \"" + task.getName() + "\" disabled");
			}
		});
	}

}
