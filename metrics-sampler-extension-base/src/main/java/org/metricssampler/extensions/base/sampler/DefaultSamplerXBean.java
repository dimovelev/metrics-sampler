package org.metricssampler.extensions.base.sampler;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.loader.xbeans.SamplerXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupRefXBean;
import org.metricssampler.config.loader.xbeans.SelectorXBean;
import org.metricssampler.config.loader.xbeans.SimpleSelectorXBean;
import org.metricssampler.config.loader.xbeans.VariableXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("sampler")
public class DefaultSamplerXBean extends SamplerXBean {
	@XStreamAsAttribute
	private String input;

	@XStreamAsAttribute
	private String outputs;

	@XStreamAsAttribute
	private Boolean quiet = false;
	
	private List<VariableXBean> variables;
	
	private List<SelectorXBean> selectors;

	public String getInput() {
		return input;
	}

	@Override
	public String getName() {
		final String name = super.getName();
		if (name == null) {
			return input;
		}
		return name;
	}


	public void setInput(final String input) {
		this.input = input;
	}

	public String getOutputs() {
		return outputs;
	}

	public void setOutputs(final String outputs) {
		this.outputs = outputs;
	}

	public List<VariableXBean> getVariables() {
		return variables;
	}

	public void setVariables(final List<VariableXBean> variables) {
		this.variables = variables;
	}

	public List<SelectorXBean> getSelectors() {
		return selectors;
	}

	public void setSelectors(final List<SelectorXBean> selectors) {
		this.selectors = selectors;
	}

	public Boolean getQuiet() {
		return quiet;
	}

	public void setQuiet(final Boolean quiet) {
		this.quiet = quiet;
	}

	@Override
	protected void validate() {
		super.validate();
		if (isInstantiatable()) {
			notEmpty("input", "default sampler", getInput());
			notEmpty("outputs", "default sampler", getOutputs());
			notEmpty("selectors", "default sampler", getSelectors());
		}
	}
	@Override
	public SamplerConfig toConfig(final Map<String, InputConfig> inputs, final Map<String, OutputConfig> outputs, final Map<String, List<SelectorConfig>> selectorTemplates, final Map<String, Object> globalVariables) {
		validate();

		final InputConfig inputConfig = configureInput(inputs);
		final List<OutputConfig> outputConfigs = configureOutputs(outputs);
		final List<SelectorConfig> selectorConfigs = configureSelectors(selectorTemplates);
		final Map<String, Object> samplerVariables = VariableXBean.toMap(getVariables());
		
		return new DefaultSamplerConfig(getName(), getInterval(), isDisabled(), inputConfig, outputConfigs, selectorConfigs, samplerVariables, globalVariables, getQuiet() != null ? getQuiet() : false);
	}

	protected List<SelectorConfig> configureSelectors(final Map<String, List<SelectorConfig>> templates) {
		final List<SelectorConfig> result = new LinkedList<SelectorConfig>();
		for (final SelectorXBean item : getSelectors()) {
			if (item instanceof SelectorGroupRefXBean) {
				result.addAll(((SelectorGroupRefXBean) item).toConfig(templates));
			} else if (item instanceof SimpleSelectorXBean) {
				result.add(((SimpleSelectorXBean) item).toConfig());
			} else {
				throw new ConfigurationException("Unsupporter selector: " + item);
			}
		}
		if (result.isEmpty()) {
			throw new ConfigurationException("Default sampler has no selectors");
		}
		return result;
	}

	protected InputConfig configureInput(final Map<String, InputConfig> inputs) {
		final InputConfig result = inputs.get(getInput());
		if (result == null) {
			throw new ConfigurationException("Input named \"" + getInput() + "\" not found");
		}
		return result;
	}

	protected List<OutputConfig> configureOutputs(final Map<String, OutputConfig> outputs) {
		final List<OutputConfig> result = new LinkedList<OutputConfig>();
		for (final String name : getOutputs().split(",")) {
			final OutputConfig output = outputs.get(name);
			if (output == null) {
				throw new ConfigurationException("Output named \"" + name + "\" not found");
			}
			result.add(output);
		}
		return result;
	}
}
