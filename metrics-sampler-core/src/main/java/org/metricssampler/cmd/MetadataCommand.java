package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.service.GlobalObjectFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Parameters(commandNames="metadata", commandDescriptionKey="help.metadata.command")
public class MetadataCommand extends ConfigurationCommand {
	@Parameter(names="-n", descriptionKey="help.param.inputs")
	protected List<String> inputs = new LinkedList<String>();

	@Override
	protected void runBootstrapped() {
		final Set<String> names = new HashSet<>();
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
