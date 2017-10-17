package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

/**
 * An XBean which has a mandatory name attribute.
 */
public abstract class NamedXBean extends XBean {
	/**
	 * @see InputConfig#getName()
	 */
	@XStreamAsAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	protected void validate() {
		notEmpty(this, "name", getName());
	}
}
