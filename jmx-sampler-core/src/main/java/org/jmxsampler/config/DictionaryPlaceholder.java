package org.jmxsampler.config;

import java.util.Map;

public class DictionaryPlaceholder extends Placeholder {
	private final Map<String, String> value;
	
	public DictionaryPlaceholder(final String key, final Map<String, String> value) {
		super(key);
		this.value = value;
	}

	@Override
	public Map<String, String> getValue() {
		return value;
	}

}
