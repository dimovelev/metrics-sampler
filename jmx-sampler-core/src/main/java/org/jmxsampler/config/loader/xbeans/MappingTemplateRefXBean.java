package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.List;
import java.util.Map;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.MappingConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("mapping-template-ref")
public class MappingTemplateRefXBean extends MappingXBean {
	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	protected void validate() {
		notEmpty("name", "mapping-template-ref", getName());
	}

	public List<MappingConfig> toConfig(final Map<String, List<MappingConfig>> mappingTemplates) {
		validate();
		final List<MappingConfig> result = mappingTemplates.get(getName());
		if (result == null) {
			throw new ConfigurationException("Mapping templated named \"" + getName() +"\" not found");
		}
		return result;
	}

}
