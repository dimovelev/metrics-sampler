package org.metricssampler.extensions.http.parsers.regexp;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.greaterThanZero;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPattern;

import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("regexp-line-format")
public class RegExpLineFormatXBean {
	@XStreamAsAttribute
	private String expression;

	@XStreamAlias("name-index")
	@XStreamAsAttribute
	private int nameIndex;

	@XStreamAlias("value-index")
	@XStreamAsAttribute
	private int valueIndex;

	public String getExpression() {
		return expression;
	}

	public void setExpression(final String expression) {
		this.expression = expression;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(final int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public int getValueIndex() {
		return valueIndex;
	}

	public void setValueIndex(final int valueIndex) {
		this.valueIndex = valueIndex;
	}

	protected void validate() {
		notEmpty(this, "expression", getExpression());
		validPattern(this, "expression", getExpression());
		greaterThanZero(this, "name-index", getNameIndex());
		greaterThanZero(this, "value-index", getValueIndex());
	}

	public RegExpLineFormat createFormat() {
		validate();
		return new RegExpLineFormat(Pattern.compile(expression), getNameIndex(), getValueIndex());
	}
}
