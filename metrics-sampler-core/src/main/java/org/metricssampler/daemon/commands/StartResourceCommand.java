package org.metricssampler.daemon.commands;

import org.metricssampler.resources.SharedResource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class StartResourceCommand extends MapEntryCommand<SharedResource> {

	protected StartResourceCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, SharedResource> resources, final String name) {
		super(reader, writer, resources, name);
	}

	@Override
	protected void processMatchingItem(final SharedResource item, final BufferedWriter writer) throws IOException {
		item.startup();
	}
}
