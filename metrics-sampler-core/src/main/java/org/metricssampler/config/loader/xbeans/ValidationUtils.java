package org.metricssampler.config.loader.xbeans;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.util.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public final class ValidationUtils {
	private ValidationUtils() {
	}

	private static String determineTagNameForBean(final Object xbean) {
		final XStreamAlias annotation = xbean.getClass().getAnnotation(XStreamAlias.class);
		if (annotation != null) {
			return annotation.value();
		}
		return StringUtils.camelCaseToSplit(xbean.getClass().getSimpleName(), "-");
	}
	
	private static String determineBeanName(final Object xbean) {
		final String tag = determineTagNameForBean(xbean);
		if (xbean instanceof NamedXBean) {
			return tag + "[" + ((NamedXBean) xbean).getName() + "]";
		} else {
			return tag;
		}
	}
	
	public static void notEmpty(final Object xbean, final String name, final String value) {
		if (value == null || value.equals("")) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
	}

	public static void notEmpty(final Object xbean, final String name, final Collection<?> value) {
		if (value == null || value.isEmpty()) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
	}

	public static void validUrl(final Object xbean, final String name, final String value) {
		notEmpty(xbean, name, value);
		try {
			new URL(value);
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is not a valid URL: " + e.getMessage());
		}
	}

	public static void validPort(final Object xbean, final String name, final Integer value) {
		if (value == null) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
		if (value < 1 || value > 65535) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean)  + " with value "+value+" is not a valid port in range [1,65535]");
		}
	}

	public static void greaterThanZero(final Object xbean, final String name, final Integer value) {
		if (value == null) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
		if (value < 1) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " with value "+value+" is not a valid number greater than 0");
		}
	}
	
	public static void notNegative(final Object xbean, final String name, final Integer value) {
		if (value == null) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
		if (value < 0) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " with value "+value+" is not a valid number greater than or equal to 0");
		}
	}
}
