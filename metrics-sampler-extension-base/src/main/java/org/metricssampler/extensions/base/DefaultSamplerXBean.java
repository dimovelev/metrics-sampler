package org.metricssampler.extensions.base;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.*;
import org.metricssampler.config.loader.xbeans.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

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

    @XStreamAlias("initial-reset-timeout")
    @XStreamAsAttribute
    private Integer initialResetTimeout;

    @XStreamAlias("regular-reset-timeout")
    @XStreamAsAttribute
    private Integer regularResetTimeout;

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

	public Integer getInitialResetTimeout() {
		return initialResetTimeout;
	}

	public void setInitialResetTimeout(final Integer initialResetTimeout) {
		this.initialResetTimeout = initialResetTimeout;
	}

    public Integer getRegularResetTimeout() {
        return regularResetTimeout;
    }

    public void setRegularResetTimeout(Integer regularResetTimeout) {
        this.regularResetTimeout = regularResetTimeout;
    }


    @Override
	protected void validate() {
		super.validate();
		if (isInstantiatable()) {
			notEmpty(this, "input", getInput());
			notEmpty(this, "selectors", getSelectors());
			if (initialResetTimeout != null) {
				greaterThanZero(this, "initial-reset-timeout", initialResetTimeout);
			}
            if (regularResetTimeout != null) {
                greaterThanZero(this, "regular-reset-timeout", regularResetTimeout);
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
        final int initialResetTimeoutInt = initialResetTimeout != null ? initialResetTimeout : -1;
        final int regularResetTimeoutInt = regularResetTimeout != null ? regularResetTimeout : -1;
		final List<ValueTransformerConfig> valueTransformerConfigs = configureValueTransformers(valueTransformers);
		return new DefaultSamplerConfig(getName(), pool, getInterval(), ignored, disabled, inputConfig, outputConfigs, selectorConfigs, samplerVariables, globalVariables, valueTransformerConfigs, quiet, initialResetTimeoutInt, regularResetTimeoutInt);
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
				throw new ConfigurationException("Unsupported selector: " + item);
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
