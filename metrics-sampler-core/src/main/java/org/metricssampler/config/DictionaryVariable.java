package org.metricssampler.config;

import java.util.Map;

/**
 * A variable whose value is a map of key and values.
 */
public class DictionaryVariable extends Variable {
	private final Map<String, String> value;
	
	public DictionaryVariable(final String name, final Map<String, String> value) {
		super(name);
		this.value = value;
	}

	@Override
	public Map<String, String> getValue() {
		return value;
	}
}
