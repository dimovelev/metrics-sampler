package org.metricssampler.cmd;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.metricssampler.resources.SamplerStats;
import org.metricssampler.sampler.Sampler;
import org.metricssampler.service.Bootstrapper;

import com.beust.jcommander.Parameter;

public abstract class SamplersCommand extends NormalCommand {
	@Parameter(names="--samplers", description = "<name>* - The names of the samplers to check. Leave empty for all.")
	protected List<String> samplers = new LinkedList<String>();

	public SamplersCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		final Set<String> names = new HashSet<String>();
		names.addAll(samplers);
		preProcess();
		for (final Sampler sampler : bootstrapper.getSamplers()) {
			if (names.isEmpty() || names.contains(sampler.getConfig().getName())) {
				SamplerStats.init();
				try {
					process(sampler);
				} catch (final RuntimeException e) {
					logger.warn("Sampler threw exception. Ignoring", e);
				}
				SamplerStats.unset();
			}
		}
		postProcess();
	}

	protected void preProcess() {
		// do nothing by default
	}
	
	protected abstract void process(Sampler sampler);
	
	protected void postProcess() {
		// do nothing by default
	}
}