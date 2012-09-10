package org.jmxsampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * XBean that can inherit all unspecified properties from another XBean of the same type. 
 */
public abstract class TemplatableXBean extends NamedXBean {
	/**
	 * The name of another named xbean that will be used as template to set default values
	 */
	@XStreamAsAttribute
	private String template;

	/**
	 * <code>true</code> if this xbean is a template. <code>false</code> otherwise.
	 */
	@XStreamAsAttribute
	@XStreamAlias("abstract")
	private boolean _abstract;
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(final String template) {
		this.template = template;
	}
	
	public boolean hasTemplate() {
		return template != null;
	}

	public boolean isAbstract() {
		return _abstract;
	}
	
	public boolean isInstantiatable() {
		return !isAbstract();
	}
	
	public void setAbstract(final boolean _abstract) {
		this._abstract = _abstract;
	}
}
