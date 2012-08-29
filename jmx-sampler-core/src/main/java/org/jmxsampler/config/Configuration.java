package org.jmxsampler.config;

import java.util.Collection;
import java.util.Collections;

public class Configuration {
	private final int poolSize;
	private final Collection<ReaderConfig> readers;
	private final Collection<WriterConfig> writers;
	private final Collection<SamplerConfig> samplers;
	private final Collection<PlaceholderConfig> placeholders;
	
	public Configuration(final int poolSize, final Collection<ReaderConfig> readers, final Collection<WriterConfig> writers, final Collection<SamplerConfig> samplers, final Collection<PlaceholderConfig> placeholders) {
		this.poolSize = poolSize;
		this.readers = readers;
		this.writers = writers;
		this.samplers = samplers;
		this.placeholders = placeholders;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public Collection<ReaderConfig> getReaders() {
		return Collections.unmodifiableCollection(readers);
	}

	public Collection<WriterConfig> getWriters() {
		return Collections.unmodifiableCollection(writers);
	}

	public Collection<SamplerConfig> getSamplers() {
		return Collections.unmodifiableCollection(samplers);
	}

	public Collection<PlaceholderConfig> getPlaceholders() {
		return Collections.unmodifiableCollection(placeholders);
	}
}
