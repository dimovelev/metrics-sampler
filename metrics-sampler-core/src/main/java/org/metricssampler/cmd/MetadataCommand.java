package org.metricssampler.cmd;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.GlobalObjectFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandNames="metadata", separators = "=", commandDescription = "Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build your rules based on that.")
public class MetadataCommand extends NormalCommand {
	@Parameter(names="--inputs", description = "<name>* - The names of the inputs to fetch metadata from.")
	protected List<String> inputs = new LinkedList<String>();

	public MetadataCommand(final MainCommand mainCommand) {
		super(mainCommand);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		final Set<String> names = new HashSet<String>();
		names.addAll(inputs);
		final GlobalObjectFactory factory = bootstrapper;
		for(final InputConfig input : bootstrapper.getConfiguration().getInputs()) {
			SamplerStats.init();
			if (names.isEmpty() || names.contains(input.getName())) {
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
}
