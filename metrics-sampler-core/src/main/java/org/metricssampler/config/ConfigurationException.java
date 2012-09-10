package org.metricssampler.config;

/**
 * Exception indicating an unrecoverable problem in the application configuration. 
 */
public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConfigurationException(final String msg) {
		super(msg);
	}

	public ConfigurationException(final Throwable e) {
		super(e);
	}

	public ConfigurationException(final String msg, final Throwable e) {
		super(msg, e);
	}

}
