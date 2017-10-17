package org.metricssampler.extensions.exec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

@XStreamAlias("argument")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class ArgumentXBean {
	private String value;

	protected void validate() {
		notEmpty(this, "value", getValue());
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
