package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * An XBean which has a mandatory name attribute.
 */
public abstract class NamedXBean {
	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	protected void validate() {
		final String className = getClass().getSimpleName();
		final String context = className.substring(0, className.length()-"XBean".length()).toLowerCase();
		notEmpty("name", context, getName());
	}
}
