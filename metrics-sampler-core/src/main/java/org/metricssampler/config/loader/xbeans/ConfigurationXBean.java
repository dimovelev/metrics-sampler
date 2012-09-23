package org.metricssampler.config.loader.xbeans;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.Configuration;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.Placeholder;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("configuration")
public class ConfigurationXBean {
	@XStreamAlias("pool-size")
	@XStreamAsAttribute
	private int poolSize;

	private List<IncludeXBean> includes;
	private List<InputXBean> inputs;
	private List<OutputXBean> outputs;
	private List<SamplerXBean> samplers;
	private List<PlaceholderXBean> placeholders;
	
	@XStreamAlias("selector-groups")
	private List<SelectorGroupXBean> selectorGroups;

	public List<IncludeXBean> getIncludes() {
		return includes;
	}

	public void setIncludes(final List<IncludeXBean> includes) {
		this.includes = includes;
	}

	public List<InputXBean> getInputs() {
		return inputs;
	}

	public void setInputs(final List<InputXBean> inputs) {
		this.inputs = inputs;
	}

	public List<OutputXBean> getOutputs() {
		return outputs;
	}

	public void setOutputs(final List<OutputXBean> outputs) {
		this.outputs = outputs;
	}

	public List<SamplerXBean> getSamplers() {
		return samplers;
	}

	public void setSamplers(final List<SamplerXBean> samplers) {
		this.samplers = samplers;
	}

	public List<SelectorGroupXBean> getSelectorGroups() {
		return selectorGroups;
	}

	public void setSelectorTemplates(final List<SelectorGroupXBean> selectorGroups) {
		this.selectorGroups = selectorGroups;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(final int poolSize) {
		this.poolSize = poolSize;
	}

	public List<PlaceholderXBean> getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(final List<PlaceholderXBean> placeholders) {
		this.placeholders = placeholders;
	}

	public Configuration toConfig() {
		final List<Placeholder> placeholders = configurePlaceholders(getPlaceholders());
		final Map<String, InputConfig> inputs = configureInputs(getInputs());
		final Map<String, OutputConfig> outputs = configureOutputs(getOutputs());
		final Map<String, List<SelectorConfig>> selectorGroups = configureSelectorGroups(getSelectorGroups());
		final List<SamplerConfig> samplers = configureSamplers(getSamplers(), inputs, outputs, selectorGroups, placeholders);
		return new Configuration(getPoolSize(), inputs.values(), outputs.values(), samplers, placeholders);
	}

	private List<Placeholder> configurePlaceholders(final List<PlaceholderXBean> items) {
		final List<Placeholder> result = new LinkedList<Placeholder>();
		if (items != null) {
			for (final PlaceholderXBean item : items) {
				result.add(item.toConfig());
			}
		}
		return result;
	}

	
	private Map<String, InputConfig> configureInputs(final List<InputXBean> list) {
		final LinkedHashMap<String, InputXBean> xbeans = TemplatableXBeanUtils.sortByDependency(list); 
		
		final Map<String, InputConfig> result = new HashMap<String, InputConfig>();
		for (final InputXBean fromItem : xbeans.values()) {
			TemplatableXBeanUtils.applyTemplate(fromItem, xbeans);
			if (fromItem.isInstantiatable()) {
				final InputConfig item = fromItem.toConfig();
				if (result.containsKey(item.getName())) {
					throw new ConfigurationException("Two inputs with the same name "+item.getName());
				}
				result.put(item.getName(), item);
			}
		}
		return result;
	}

	private Map<String, OutputConfig> configureOutputs(final List<OutputXBean> list) {
		final Map<String, OutputConfig> result = new HashMap<String, OutputConfig>();
		for (final OutputXBean fromItem : list) {
			final OutputConfig item = fromItem.toConfig();
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two outputs with the same name "+item.getName());
			}
			result.put(item.getName(), item);
		}
		return result;
	}

	private Map<String, List<SelectorConfig>> configureSelectorGroups(final List<SelectorGroupXBean> items) {
		final Map<String, List<SelectorConfig>> result = new HashMap<String, List<SelectorConfig>>();
		for (final SelectorGroupXBean item : items) {
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two selector groups with the same name \""+item.getName() + "\"");
			}
			result.put(item.getName(), item.toConfig());
		}
		return result;
	}

	private List<SamplerConfig> configureSamplers(final List<SamplerXBean> samplers, final Map<String, InputConfig> inputs, final Map<String, OutputConfig> outputs, final Map<String, List<SelectorConfig>> selectorGroups, final List<Placeholder> placeholders) {
		final LinkedHashMap<String, SamplerXBean> namedSamplers = TemplatableXBeanUtils.sortByDependency(samplers); 

		final List<SamplerConfig> result = new LinkedList<SamplerConfig>();
		for (final SamplerXBean def : samplers) {
			TemplatableXBeanUtils.applyTemplate(def, namedSamplers);
			if (def.isInstantiatable()) {
				result.add(def.toConfig(inputs, outputs, selectorGroups, placeholders));
			}
		}
		return result;
	}

	public void include(final ConfigurationXBean includeConfig) {
		inputs = addAllToList(inputs, includeConfig.getInputs());
		outputs = addAllToList(outputs, includeConfig.getOutputs());
		placeholders = addAllToList(placeholders, includeConfig.getPlaceholders());
		selectorGroups = addAllToList(selectorGroups, includeConfig.getSelectorGroups());
		samplers = addAllToList(samplers, includeConfig.getSamplers());
	}
	
	private <T> List<T> addAllToList(final List<T> destination, final List<T> source) {
		final List<T> result = destination != null ? destination : new LinkedList<T>();
		if (source != null) {
			for (final T item : source) {
				result.add(item);
			}
		}
		return result;
	}
}
