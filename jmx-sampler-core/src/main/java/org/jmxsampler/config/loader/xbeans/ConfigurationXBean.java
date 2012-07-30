package org.jmxsampler.config.loader.xbeans;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmxsampler.config.Configuration;
import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("configuration")
public class ConfigurationXBean {
	@XStreamAlias("pool-size")
	@XStreamAsAttribute
	private int poolSize;

	private List<ReaderXBean> readers;

	private List<WriterXBean> writers;

	private List<SamplerXBean> samplers;

	@XStreamAlias("mapping-templates")
	private List<MappingTemplateXBean> mappingTemplates;

	public List<ReaderXBean> getReaders() {
		return readers;
	}

	public void setReaders(final List<ReaderXBean> readers) {
		this.readers = readers;
	}

	public List<WriterXBean> getWriters() {
		return writers;
	}

	public void setWriters(final List<WriterXBean> writers) {
		this.writers = writers;
	}

	public List<SamplerXBean> getSamplers() {
		return samplers;
	}

	public void setSamplers(final List<SamplerXBean> samplers) {
		this.samplers = samplers;
	}

	public List<MappingTemplateXBean> getMappingTemplates() {
		return mappingTemplates;
	}

	public void setMappingTemplates(final List<MappingTemplateXBean> mappingTemplates) {
		this.mappingTemplates = mappingTemplates;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(final int poolSize) {
		this.poolSize = poolSize;
	}

	public Configuration toConfig() {
		final Map<String, ReaderConfig> readers = configureReaders(getReaders());
		final Map<String, WriterConfig> writers = configureWriters(getWriters());
		final Map<String, List<MappingConfig>> mappingTemplates = configureMappingTemplates(getMappingTemplates());
		final List<SamplerConfig> mappers = configureSamplers(getSamplers(), readers, writers, mappingTemplates);

		return new Configuration(getPoolSize(), readers.values(), writers.values(), mappers);
	}

	private Map<String, ReaderConfig> configureReaders(final List<ReaderXBean> list) {
		final Map<String, ReaderConfig> result = new HashMap<String, ReaderConfig>();
		for (final ReaderXBean fromItem : list) {
			final ReaderConfig item = fromItem.toConfig();
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two readers with the same name "+item.getName());
			}
			result.put(item.getName(), item);
		}
		return result;
	}

	private Map<String, WriterConfig> configureWriters(final List<WriterXBean> list) {
		final Map<String, WriterConfig> result = new HashMap<String, WriterConfig>();
		for (final WriterXBean fromItem : list) {
			final WriterConfig item = fromItem.toConfig();
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two writers with the same name "+item.getName());
			}
			result.put(item.getName(), item);
		}
		return result;
	}

	private Map<String, List<MappingConfig>> configureMappingTemplates(final List<MappingTemplateXBean> items) {
		final Map<String, List<MappingConfig>> result = new HashMap<String, List<MappingConfig>>();
		for (final MappingTemplateXBean item : items) {
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two mapping templates with the same name "+item.getName());
			}
			result.put(item.getName(), item.toConfig());
		}
		return result;
	}

	private List<SamplerConfig> configureSamplers(final List<SamplerXBean> samplers, final Map<String, ReaderConfig> readers, final Map<String, WriterConfig> writers, final Map<String, List<MappingConfig>> mappingTemplates) {
		final List<SamplerConfig>result = new LinkedList<SamplerConfig>();
		for (final SamplerXBean def : samplers) {
			result.add(def.toConfig(readers, writers, mappingTemplates));
		}
		return result;
	}
}
