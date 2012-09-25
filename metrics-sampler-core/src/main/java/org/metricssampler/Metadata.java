package org.metricssampler;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.Bootstrapper;
import org.metricssampler.service.GlobalObjectFactory;

public class Metadata extends NormalRunner {

	public static void main(final String[] args) {
		new Metadata().run(args);
	}

	@Override
	protected void run(final Bootstrapper bootstrapper) {
		final GlobalObjectFactory factory = (GlobalObjectFactory) bootstrapper;
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
