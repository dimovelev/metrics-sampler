package org.metricssampler;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.GlobalObjectFactory;

public class Metadata extends NormalRunner {

	public static void main(final String[] args) {
		new Metadata().run(args);
	}

	@Override
	protected void runBootstrapped(final Bootstrapper bootstrapper) {
		SamplerStats.set(new SamplerStats());
		final GlobalObjectFactory factory = bootstrapper;
		for(final InputConfig input : bootstrapper.getConfiguration().getInputs()) {
			final MetricsReader reader = factory.newReaderForInput(input);
			reader.open();
			System.out.println("Reader: " + input.getName());
			for(final MetricName item : reader.readNames()) {
				System.out.println("\tName:" + item.getName());
				System.out.println("\tDescription:" + item.getDescription());
			}
			reader.close();
		}
	}

}
