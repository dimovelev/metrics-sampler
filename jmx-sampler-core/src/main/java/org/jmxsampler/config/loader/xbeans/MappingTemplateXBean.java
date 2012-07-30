package org.jmxsampler.config.loader.xbeans;

import java.util.LinkedList;
import java.util.List;

import org.jmxsampler.config.MappingConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("mapping-template")
public class MappingTemplateXBean extends NamedXBean {
	@XStreamImplicit
	private List<SimpleMappingXBean> mappings;

	public List<SimpleMappingXBean> getMappings() {
		return mappings;
	}

	public void setMappings(final List<SimpleMappingXBean> mappings) {
		this.mappings = mappings;
	}
	public List<MappingConfig> toConfig() {
		validate();
		final List<MappingConfig> result = new LinkedList<MappingConfig>();
		for (final SimpleMappingXBean item : getMappings()) {
			result.add(item.toConfig());
		}
		return result;
	}
}
