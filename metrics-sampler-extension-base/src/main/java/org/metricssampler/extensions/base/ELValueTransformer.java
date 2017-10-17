package org.metricssampler.extensions.base;

import org.metricssampler.values.NameRegExpValueTransformer;

import javax.el.ELContext;
import javax.el.ValueExpression;

public class ELValueTransformer extends NameRegExpValueTransformer {
	private final ELFactory elFactory;

	public ELValueTransformer(final ELValueTransformerConfig config, final ELFactory elFactory) {
		super(config);
		this.elFactory = elFactory;
	}

	private ELValueTransformerConfig getConfig() {
		return (ELValueTransformerConfig) config;
	}

	@Override
	public String transform(final String value) {
		final ELContext context = elFactory.newContext(value);
		final ValueExpression expression = elFactory.getFactory().createValueExpression(context, "#{" + getConfig().getExpression() + "}", Object.class); 
		final Object result = expression.getValue(context);
		return result != null ? result.toString() : null;
	}
}
