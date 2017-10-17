package org.metricssampler.daemon.commands;

import org.metricssampler.resources.SamplerTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class ResetSamplerControlCommand extends MapEntryCommand<SamplerTask> {
	protected ResetSamplerControlCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name) {
		super(reader, writer, tasks, name);
	}

	@Override
	protected void processMatchingItem(final SamplerTask task, final BufferedWriter writer) throws IOException {
		logger.info("Resetting sampler \"{}\"", task.getName());
		task.reset();
		respond("Sampler \"" + task.getName() + "\" resetted");
	}
}
