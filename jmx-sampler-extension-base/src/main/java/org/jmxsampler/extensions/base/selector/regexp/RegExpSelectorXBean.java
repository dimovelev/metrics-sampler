package org.jmxsampler.extensions.base.selector.regexp;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.SelectorConfig;
import org.jmxsampler.config.loader.xbeans.SimpleSelectorXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("regexp")
public class RegExpSelectorXBean extends SimpleSelectorXBean {
	@XStreamAlias("from-name")
	@XStreamAsAttribute
	private String fromName;

	@XStreamAlias("from-description")
	@XStreamAsAttribute
	private String fromDescription;

	@XStreamAlias("to-name")
	@XStreamAsAttribute
	private String toName;

	public String getFromName() {
		return fromName;
	}
	public void setFromName(final String fromName) {
		this.fromName = fromName;
	}
	public String getFromDescription() {
		return fromDescription;
	}
	public void setFromDescription(final String fromDescription) {
		this.fromDescription = fromDescription;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(final String toName) {
		this.toName = toName;
	}
	protected void validate() {
		if ((fromName == null || fromName.equals("")) && (fromDescription == null || fromDescription.equals(""))) {
			throw new ConfigurationException("Either from-name or from-description must be set for regexp selector");
		}
		notEmpty("to-name", "regexp selector", getToName());
	}
	@Override
	public SelectorConfig toConfig() {
		validate();
		return new RegExpSelectorConfig(getFromName(), getFromDescription(), getToName());
	}
}
