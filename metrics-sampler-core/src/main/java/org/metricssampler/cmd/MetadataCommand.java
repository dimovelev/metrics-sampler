package org.metricssampler.cmd;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.GlobalObjectFactory;

import com.beust.jcommander.Parameters;

@Parameters(commandNames="metadata", separators = "=", commandDescription = "Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build your rules based on that.")
public class MetadataCommand extends NormalCommand {
	public MetadataCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		final GlobalObjectFactory factory = bootstrapper;
		for(final InputConfig input : bootstrapper.getConfiguration().getInputs()) {
			SamplerStats.init();
			final MetricsReader reader = factory.newReaderForInput(input);
			reader.open();
			System.out.println("Reader: " + input.getName());
			for(final MetricName item : reader.readNames()) {
				System.out.println("\tName:" + item.getName());
				System.out.println("\tDescription:" + item.getDescription());
			}
			reader.close();
			SamplerStats.unset();
		}
	}
}
