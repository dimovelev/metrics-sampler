package org.jmxsampler.extensions.base.sampler;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.MappingConfig;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.SamplerConfig;
import org.jmxsampler.config.WriterConfig;
import org.jmxsampler.config.loader.xbeans.MappingTemplateRefXBean;
import org.jmxsampler.config.loader.xbeans.MappingXBean;
import org.jmxsampler.config.loader.xbeans.SamplerXBean;
import org.jmxsampler.config.loader.xbeans.SimpleMappingXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("default-sampler")
public class DefaultSamplerXBean extends SamplerXBean {
	@XStreamAsAttribute
	private String reader;

	@XStreamAsAttribute
	private String writers;

	private List<MappingXBean> mappings;

	public String getReader() {
		return reader;
	}

	public void setReader(final String reader) {
		this.reader = reader;
	}

	public String getWriters() {
		return writers;
	}

	public void setWriters(final String writers) {
		this.writers = writers;
	}

	public List<MappingXBean> getMappings() {
		return mappings;
	}
	public void setMappings(final List<MappingXBean> mappings) {
		this.mappings = mappings;
	}
	@Override
	protected void validate() {
		super.validate();
		notEmpty("reader", "default sampler", getReader());
		notEmpty("writers", "default sampler", getWriters());
		notEmpty("mappings", "default sampler", getMappings());
	}
	@Override
	public SamplerConfig toConfig(final Map<String, ReaderConfig> readers, final Map<String, WriterConfig> writers, final Map<String, List<MappingConfig>> mappingTemplates) {
		validate();
		final ReaderConfig readerConfig = readers.get(getReader());
		if (readerConfig == null) {
			throw new ConfigurationException("Reader named \"" + getReader() + "\" not found");
		}

		final List<WriterConfig> writerConfigs = new LinkedList<WriterConfig>();
		for (final String name : getWriters().split(",")) {
			final WriterConfig writer = writers.get(name);
			if (writer == null) {
				throw new ConfigurationException("Writer named \"" + name + "\" not found");
			}
			writerConfigs.add(writer);
		}

		final List<MappingConfig> mappingConfigs = new LinkedList<MappingConfig>();
		for (final MappingXBean item : getMappings()) {
			if (item instanceof MappingTemplateRefXBean) {
				mappingConfigs.addAll(((MappingTemplateRefXBean) item).toConfig(mappingTemplates));
			} else if (item instanceof SimpleMappingXBean) {
				mappingConfigs.add(((SimpleMappingXBean) item).toConfig());
			} else {
				throw new ConfigurationException("Unsupporter mapping: " + item);
			}
		}
		if (mappingConfigs.isEmpty()) {
			throw new ConfigurationException("Default sampler has no mappings");
		}
		return new DefaultSamplerConfig(getInterval(), isDisabled(), readerConfig, writerConfigs, mappingConfigs);
	}
}
