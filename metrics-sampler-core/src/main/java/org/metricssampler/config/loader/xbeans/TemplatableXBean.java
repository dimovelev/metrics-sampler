package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * XBean that can inherit all unspecified properties from another XBean of the same type. 
 */
public abstract class TemplatableXBean extends NamedXBean {
	/**
	 * The name of another named xbean that will be used as template to set default values
	 */
	@XStreamAsAttribute
	private String parent;

	/**
	 * <code>true</code> if this xbean is a template. <code>false</code> otherwise.
	 */
	@XStreamAsAttribute
	private boolean template;
	
	public String getParent() {
		return parent;
	}

	public void setParent(final String parent) {
		this.parent = parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}

	public boolean isTemplate() {
		return template;
	}
	
	public boolean isInstantiatable() {
		return !isTemplate();
	}
	
	public void setTemplate(final boolean template) {
		this.template = template;
	}
}
