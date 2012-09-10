package org.jmxsampler.config.loader.xbeans;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.List;
import java.util.Map;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.SelectorConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("use-group")
public class SelectorGroupRefXBean extends SelectorXBean {
	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	protected void validate() {
		notEmpty("name", "use-group", getName());
	}

	public List<SelectorConfig> toConfig(final Map<String, List<SelectorConfig>> selectorGroups) {
		validate();
		final List<SelectorConfig> result = selectorGroups.get(getName());
		if (result == null) {
			throw new ConfigurationException("Selector group named \"" + getName() +"\" not found");
		}
		return result;
	}

}
