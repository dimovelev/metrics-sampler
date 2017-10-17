package org.metricssampler.daemon.commands;

import org.metricssampler.resources.SamplerTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class EnableSamplerCommand extends MapEntryCommand<SamplerTask> {
	private final int times;
	private final long seconds;

	protected EnableSamplerCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SamplerTask> tasks, final String name, final int times, final long seconds) {
		super(reader, writer, tasks, name);
		this.times = times;
		this.seconds = seconds;
	}

	@Override
	protected void processMatchingItem(final SamplerTask task, final BufferedWriter writer) throws IOException {
		if (seconds != -1) {
			logger.info("Enabling sampler \"{}\" for {} seconds", task.getName(), seconds);
			task.enableForDuration(seconds);
			respond("Sampler \"" + task.getName() + "\" enabled for " + seconds + " seconds");
		} else if (times == -1) {
			logger.info("Enabling sampler \"{}\"", task.getName(), times);
			task.enable();
			respond("Sampler \"" + task.getName() + "\" enabled");
		} else {
			logger.info("Enabling sampler \"{}\" for {} samplings", task.getName(), times);
			task.enableForTimes(times);
			respond("Sampler \"" + task.getName() + "\" enabled for " + times + " times");
		}
	}
}
