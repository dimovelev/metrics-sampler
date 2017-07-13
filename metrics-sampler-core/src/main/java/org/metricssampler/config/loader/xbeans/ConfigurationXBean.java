package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.*;

import java.util.*;

@XStreamAlias("configuration")
public class ConfigurationXBean extends XBean {
	private List<IncludeXBean> includes;
	private List<InputXBean> inputs;
	private List<OutputXBean> outputs;
	private List<SamplerXBean> samplers;
	private List<VariableXBean> variables;

	@XStreamAlias("shared-resources")
	private List<SharedResourceXBean> sharedResources;

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

	public List<VariableXBean> getVariables() {
		return variables;
	}

	public void setVariables(final List<VariableXBean> variables) {
		this.variables = variables;
	}

	public List<SharedResourceXBean> getSharedResources() {
		return sharedResources;
	}

	public void setSharedResources(final List<SharedResourceXBean> sharedResources) {
		this.sharedResources = sharedResources;
	}

	public Configuration toConfig() {
		final Map<String, Object> globalVariables = VariableXBean.toMap(getVariables());
		final Map<String, SharedResourceConfig> sharedResources = configureSharedResources(getSharedResources());
		final Map<String, InputConfig> inputs = configureInputs(getInputs());
		final Map<String, OutputConfig> outputs = configureOutputs(getOutputs());
		final Map<String, List<SelectorConfig>> selectorGroups = configureSelectorGroups(getSelectorGroups());
		final List<SamplerConfig> samplers = configureSamplers(getSamplers(), inputs, outputs, selectorGroups, globalVariables);
		return new Configuration(inputs.values(), outputs.values(), samplers, globalVariables, sharedResources);
	}

	private Map<String, InputConfig> configureInputs(final List<InputXBean> list) {
		final LinkedHashMap<String, InputXBean> xbeans = TemplatableXBeanUtils.sortByDependency(list);

		final Map<String, InputConfig> result = new HashMap<>();
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
		final Map<String, OutputConfig> result = new HashMap<>();
		if (list != null) {
			for (final OutputXBean fromItem : list) {
				final OutputConfig item = fromItem.toConfig();
				if (result.containsKey(item.getName())) {
					throw new ConfigurationException("Two outputs with the same name "+item.getName());
				}
				result.put(item.getName(), item);
			}
		}
		return result;
	}

	private Map<String, List<SelectorConfig>> configureSelectorGroups(final List<SelectorGroupXBean> items) {
		final Map<String, SelectorGroupXBean> groups = new HashMap<>(items != null ? items.size() : 0);

		if (items != null) {
			for (final SelectorGroupXBean item : items) {
				if (groups.containsKey(item.getName())) {
					throw new ConfigurationException("Two selector groups with the same name \"" + item.getName() + "\"");
				}
				groups.put(item.getName(), item);
			}
		}

		final Map<String, List<SelectorConfig>> result = new HashMap<>();
		for (final SelectorGroupXBean item : groups.values()) {
			result.put(item.getName(), item.toConfig(groups));
		}
		return result;
	}

	private List<SamplerConfig> configureSamplers(final List<SamplerXBean> samplers, final Map<String, InputConfig> inputs, final Map<String, OutputConfig> outputs, final Map<String, List<SelectorConfig>> selectorGroups, final Map<String, Object> globalVariables) {
		final LinkedHashMap<String, SamplerXBean> namedSamplers = TemplatableXBeanUtils.sortByDependency(samplers);

		final List<SamplerConfig> result = new LinkedList<SamplerConfig>();
		if (samplers != null) {
			for (final SamplerXBean def : samplers) {
				TemplatableXBeanUtils.applyTemplate(def, namedSamplers);
				if (def.isInstantiatable()) {
					result.add(def.toConfig(inputs, outputs, selectorGroups, globalVariables));
				}
			}
		}
		return result;
	}

	private Map<String, SharedResourceConfig> configureSharedResources(final List<SharedResourceXBean> sharedResources) {
		if (sharedResources == null) {
			return Collections.emptyMap();
		}
		final Map<String, SharedResourceConfig> result = new HashMap<>(sharedResources.size());
		for (final SharedResourceXBean item : sharedResources) {
			if (result.containsKey(item.getName())) {
				throw new ConfigurationException("Two shared resources with the same name \"" + item.getName() + "\"");
			} else {
				result.put(item.getName(), item.toConfig());
			}
		}
		return result;
	}

	public void include(final ConfigurationXBean includeConfig) {
		inputs = addAllToList(inputs, includeConfig.getInputs());
		outputs = addAllToList(outputs, includeConfig.getOutputs());
		variables = addAllToList(variables, includeConfig.getVariables());
		selectorGroups = addAllToList(selectorGroups, includeConfig.getSelectorGroups());
		samplers = addAllToList(samplers, includeConfig.getSamplers());
		sharedResources = addAllToList(sharedResources, includeConfig.getSharedResources());
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
