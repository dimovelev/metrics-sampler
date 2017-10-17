package org.metricssampler.extensions.base;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.loader.xbeans.NameRegExpValueTransformerXBean;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

@XStreamAlias("el-value-transformer")
public class ELValueTransformerXBean extends NameRegExpValueTransformerXBean {
	@XStreamAlias("expression")
	@XStreamAsAttribute
	private String expression;
	
	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "expression", expression);
	}
	
	@Override
	public ELValueTransformerConfig toConfig() {
		validate();
		return new ELValueTransformerConfig(getNamePattern(), expression);
	}
}
