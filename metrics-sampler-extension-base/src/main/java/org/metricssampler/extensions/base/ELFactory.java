package org.metricssampler.extensions.base;

import de.odysseus.el.util.SimpleContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ELFactory {
	private final ExpressionFactory factory;
	private final Map<String, Method> functions = new HashMap<>();
	
	public ELFactory() {
		factory = ExpressionFactory.newInstance();

		try {
			final Method parseInt = Integer.class.getMethod("parseInt", String.class);
			final Method parseLong = Long.class.getMethod("parseLong", String.class);
			final Method substring = ELFactory.class.getMethod("substr", String.class, int.class, int.class);
			final Method back = ELFactory.class.getMethod("back", String.class, int.class);

			functions.put("c:int", parseInt);
			functions.put("c:long", parseLong);
			functions.put("s:substr", substring);
			functions.put("s:back", back);
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (final SecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	public ELContext newContext(final String value) {
		final SimpleContext result = new SimpleContext();
		for (final Entry<String, Method> entry : functions.entrySet()) {
			final int colonIdx = entry.getKey().indexOf(':');
			result.setFunction(entry.getKey().substring(0, colonIdx), entry.getKey().substring(colonIdx+1), entry.getValue());
		}
		result.setVariable("value", factory.createValueExpression(value, String.class));
		return result;
	}

	public ExpressionFactory getFactory() {
		return factory;
	}
	
	public static String substr(final String value, final int beginIndex, final int endIndex) {
		return value != null ? value.substring(beginIndex, endIndex) : null;
	}
	
	public static String back(final String value, final int beginIndex) {
		return value != null ? value.substring(beginIndex) : null;
	}
}
