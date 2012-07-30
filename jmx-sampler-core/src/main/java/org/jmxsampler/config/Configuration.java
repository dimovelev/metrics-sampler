package org.jmxsampler.config;

import java.util.Collection;
import java.util.Collections;

public class Configuration {
	private final int poolSize;
	private final Collection<ReaderConfig> readers;
	private final Collection<WriterConfig> writers;
	private final Collection<SamplerConfig> samplers;

	public Configuration(final int poolSize, final Collection<ReaderConfig> readers, final Collection<WriterConfig> writers, final Collection<SamplerConfig> samplers) {
		this.poolSize = poolSize;
		this.readers = readers;
		this.writers = writers;
		this.samplers = samplers;
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


}
