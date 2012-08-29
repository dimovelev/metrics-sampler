package org.jmxsampler.config;

import java.util.Map;

public class DictionaryPlaceholderConfig extends PlaceholderConfig {
	private final Map<String, String> value;
	
	public DictionaryPlaceholderConfig(final String key, final Map<String, String> value) {
		super(key);
		this.value = value;
	}

	@Override
	public Map<String, String> getValue() {
		return value;
	}

}
