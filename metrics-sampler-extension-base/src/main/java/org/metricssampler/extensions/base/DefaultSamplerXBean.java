package org.metricssampler.extensions.base;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.OutputConfig;
import org.metricssampler.config.SamplerConfig;
import org.metricssampler.config.SelectorConfig;
import org.metricssampler.config.ValueTransformerConfig;
import org.metricssampler.config.loader.xbeans.SamplerXBean;
import org.metricssampler.config.loader.xbeans.SelectorGroupRefXBean;
import org.metricssampler.config.loader.xbeans.SelectorXBean;
import org.metricssampler.config.loader.xbeans.SimpleSelectorXBean;
import org.metricssampler.config.loader.xbeans.ValueTransformerXBean;
import org.metricssampler.config.loader.xbeans.VariableXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("sampler")
public class DefaultSamplerXBean extends SamplerXBean {
	private static final String DEFAULT_POOL_NAME = "samplers";

	@XStreamAsAttribute
	private String input;

	@XStreamAsAttribute
	private String outputs;

	@XStreamAsAttribute
	private String pool;

	@XStreamAsAttribute
	private Boolean quiet = false;

	@XStreamAlias("reset-timeout")
	@XStreamAsAttribute
	private Integer resetTimeout;

	private List<VariableXBean> variables;

	private List<SelectorXBean> selectors;

	@XStreamAlias("value-transformers")
	private List<ValueTransformerXBean> valueTransformers;
	
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

	public String getPool() {
		return pool;
	}

	public void setPool(final String pool) {
		this.pool = pool;
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

	public Integer getResetTimeout() {
		return resetTimeout;
	}

	public void setResetTimeout(final Integer resetTimeout) {
		this.resetTimeout = resetTimeout;
	}

	@Override
	protected void validate() {
		super.validate();
		if (isInstantiatable()) {
			notEmpty(this, "input", getInput());
			notEmpty(this, "selectors", getSelectors());
			if (resetTimeout != null) {
				greaterThanZero(this, "reload-timeout", resetTimeout);
			}
		}
	}
	@Override
	public SamplerConfig toConfig(final Map<String, InputConfig> inputs, final Map<String, OutputConfig> outputs, final Map<String, List<SelectorConfig>> selectorTemplates, final Map<String, Object> globalVariables) {
		validate();

		final InputConfig inputConfig = configureInput(inputs);
		final List<OutputConfig> outputConfigs = configureOutputs(outputs);
		final List<SelectorConfig> selectorConfigs = configureSelectors(selectorTemplates);
		final Map<String, Object> samplerVariables = VariableXBean.toMap(getVariables());
		final boolean ignored = getIgnored() != null ? getIgnored() : false;
		final boolean disabled = getDisabled() != null ? getDisabled() : false;
		final boolean quiet = getQuiet() != null ? getQuiet() : false;
		final String pool = getPool() != null ? getPool() : DEFAULT_POOL_NAME;
		final int resetTimeoutInt = resetTimeout != null ? resetTimeout : -1;
		final List<ValueTransformerConfig> valueTransformerConfigs = configureValueTransformers(valueTransformers);
		return new DefaultSamplerConfig(getName(), pool, getInterval(), ignored, disabled, inputConfig, outputConfigs, selectorConfigs, samplerVariables, globalVariables, valueTransformerConfigs, quiet, resetTimeoutInt);
	}

	protected List<ValueTransformerConfig> configureValueTransformers(final List<ValueTransformerXBean> valueTransformers) {
		if (valueTransformers == null) {
			return new LinkedList<ValueTransformerConfig>();
		}
		final List<ValueTransformerConfig> result = new ArrayList<ValueTransformerConfig>(valueTransformers.size());
		for (final ValueTransformerXBean item : valueTransformers) {
			result.add(item.toConfig());
		}
		return result;
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
		if (getOutputs() == null) {
			for (final OutputConfig output : outputs.values()) {
				if (output.isDefault()) {
					result.add(output);
				}
			}
			if (result.isEmpty()) {
				throw new ConfigurationException("No outputs specified for sampler \"" + getName() + "\" and no default outputs found");
			}
		} else {
			for (final String name : getOutputs().split(",")) {
				final OutputConfig output = outputs.get(name);
				if (output == null) {
					throw new ConfigurationException("Output named \"" + name + "\" not found");
				}
				result.add(output);
			}
		}
		return result;
	}
}
