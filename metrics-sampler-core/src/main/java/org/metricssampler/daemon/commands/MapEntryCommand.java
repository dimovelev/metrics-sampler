package org.metricssampler.daemon.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Match all keys of a map against a given regular expression and do something with the matching values.
 *
 * @param <T> The type of the map values.
 */
public abstract class MapEntryCommand<T> extends BaseControlCommand {
	private final Map<String, T> items;
	private final String name;

	public MapEntryCommand(final BufferedReader reader, final BufferedWriter writer, final Map<String, T> items, final String name) {
		super(reader, writer);
		this.items = items;
		this.name = name;
	}

	protected void before() throws IOException {

	}

	protected void after(final int count) throws IOException {

	}

	@Override
	public void execute() throws IOException {
		try {
			final Pattern nameExpression = Pattern.compile(name);
			int matchingItems = 0;
			before();
			for (final Entry<String, T> entry : items.entrySet()) {
				if (nameExpression.matcher(entry.getKey()).matches()) {
					logger.debug("Processing item \"{}\" because it matches \"{}\"", entry.getKey(), nameExpression);
					processMatchingItem(entry.getValue(), writer);
					matchingItems++;
				}
			}
			if (matchingItems == 0) {
				respond("No items found matching regular expression \"" + name + "\"");
			} else {
				after(matchingItems);
			}
		} catch (final PatternSyntaxException e) {
			respond("Could not compile item name regular expression \"" + name +"\": " + e.getMessage());
		}
	}

	protected abstract void processMatchingItem(T item, BufferedWriter writer) throws IOException;

}