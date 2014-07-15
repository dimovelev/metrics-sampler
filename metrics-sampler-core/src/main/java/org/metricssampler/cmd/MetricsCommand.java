package org.metricssampler.cmd;

import com.beust.jcommander.Parameters;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.sampler.Sampler;

import java.util.Set;

@Parameters(commandNames="metrics", commandDescriptionKey="help.metrics.command")
public class MetricsCommand extends SamplersCommand {
	@Override
	protected void process(final Sampler sampler) {
		logger.info("Listing metrics of {}", sampler);
		try {
			Set<String> metrics = sampler.metrics();
            for (final String name : metrics) {
                System.out.println(name);
            }
		} catch (final MetricReadException e) {
			logger.warn("Sampler threw exception during check", e);
		}
	}
}
