package org.jmxsampler.extensions.base.sampler;

import java.util.Collections;
import java.util.List;

import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.PlaceholderConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;

public class DefaultSamplerConfig extends SamplerConfig {
	private final ReaderConfig reader;
	private final List<WriterConfig> writers;
	private final List<MappingConfig> mappings;
	private final List<PlaceholderConfig> placeholders;
	
	public DefaultSamplerConfig(final int interval, final boolean disabled, final ReaderConfig reader, final List<WriterConfig> writers, final List<MappingConfig> mappings, final List<PlaceholderConfig> placeholders) {
		super(interval, disabled);
		this.reader = reader;
		this.writers = writers;
		this.mappings = mappings;
		this.placeholders = placeholders;
	}

	public ReaderConfig getReader() {
		return reader;
	}

	public List<WriterConfig> getWriters() {
		return Collections.unmodifiableList(writers);
	}

	public List<MappingConfig> getMappings() {
		return Collections.unmodifiableList(mappings);
	}

	public List<PlaceholderConfig> getPlaceholders() {
		return placeholders;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + reader + "->" + writers + "]";
	}
	
	
}
