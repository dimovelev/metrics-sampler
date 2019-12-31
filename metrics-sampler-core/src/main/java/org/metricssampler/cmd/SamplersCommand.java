package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.sampler.Sampler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A command that can operate on a subset of the samplers given as command line argument. If the argument is not specified then the command
 * operates on all samplers.
 */
public abstract class SamplersCommand extends ConfigurationCommand {
	@Parameter(names = "-n", descriptionKey="help.param.samplers")
	protected List<String> samplers = new LinkedList<>();

	@Override
	protected void runBootstrapped() {
		final Set<String> names = new HashSet<>();
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

	/**
	 * Override this method if you require processing before the loop over the samplers
	 */
	protected void preProcess() {
		// do nothing by default
	}

	/**
	 * Invoked within the loop over the samplers
	 *
	 * @param sampler the sampler to process
	 */
	protected abstract void process(Sampler sampler);

	/**
	 * Override this method if you require processing after the loop over the samplers
	 */
	protected void postProcess() {
		// do nothing by default
	}
}