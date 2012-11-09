package org.metricssampler.config;


/**
 * Base class for output configurations.
 */
public abstract class OutputConfig extends NamedConfig {
	private final boolean default_;
	
	public OutputConfig(final String name, final boolean default_) {
		super(name);
		this.default_ = default_;
	}
	
	/**
	 * @return {@code true} if the output should be used for samplers that do not explicitly specify their outputs
	 */
	public boolean isDefault() {
		return default_;
	}
}
