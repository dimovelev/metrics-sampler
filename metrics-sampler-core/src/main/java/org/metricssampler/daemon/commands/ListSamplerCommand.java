package org.metricssampler.daemon.commands;

import org.metricssampler.resources.SamplerTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class ListSamplerCommand extends MapEntryCommand<SamplerTask> {
	private final StringBuilder samplers = new StringBuilder();

	protected ListSamplerCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name) {
		super(reader, writer, tasks, name);
	}

	@Override
	protected void processMatchingItem(final SamplerTask task, final BufferedWriter writer) throws IOException {
		samplers.append(task.getName()).append(' ');
	}

	@Override
	protected void after(final int count) throws IOException {
		respond(samplers.toString().trim());
	}
}
