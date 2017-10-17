package org.metricssampler.daemon.commands;

import org.metricssampler.resources.SamplerTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class DisableSamplerControlCommand extends MapEntryCommand<SamplerTask> {
	protected DisableSamplerControlCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name) {
		super(reader, writer, tasks, name);
	}

	@Override
	protected void processMatchingItem(final SamplerTask item, final BufferedWriter writer) throws IOException {
		logger.info("Disabling sampler \"{}\"", item.getName());
		item.disable();
		respond("Sampler \"" + item.getName() + "\" disabled");
	}
}
