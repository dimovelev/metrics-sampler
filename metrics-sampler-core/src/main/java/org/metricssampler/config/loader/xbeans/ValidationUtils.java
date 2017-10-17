package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.ConfigurationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.metricssampler.util.StringUtils.camelCaseToSplit;

public final class ValidationUtils {
	private ValidationUtils() {
	}

	private static String determineTagNameForBean(final Object xbean) {
		final XStreamAlias annotation = xbean.getClass().getAnnotation(XStreamAlias.class);
		if (annotation != null) {
			return annotation.value();
		}
		return camelCaseToSplit(xbean.getClass().getSimpleName(), "-");
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

	public static void notNull(final Object xbean, final String name, final Object value) {
		if (value == null) {
			throw new ConfigurationException("Element \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
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
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean)  + " with value " + value + " is not a valid port in range [1,65535]");
		}
	}

	public static void greaterThanZero(final Object xbean, final String name, final Integer value) {
		if (value == null) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
		if (value < 1) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " with value " + value + " is not a valid number greater than 0");
		}
	}

	public static void notNegative(final Object xbean, final String name, final Integer value) {
		if (value == null) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " is mandatory");
		}
		if (value < 0) {
			throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " with value " + value + " is not a valid number greater than or equal to 0");
		}
	}

	public static void notNegativeOptional(final Object xbean, final String name, final Integer value) {
		if (value != null) {
			if (value < 0) {
				throw new ConfigurationException("Attribute \"" + name + "\" of " + determineBeanName(xbean) + " with value " + value + " is not a valid number greater than or equal to 0");
			}
		}
	}

	public static void validPattern(final Object xbean, final String name, final String value) {
		if (value != null) {
			try {
				Pattern.compile(value);
			} catch (final PatternSyntaxException e) {
				throw new ConfigurationException("Value \"" + value + "\" of attribute \"" + name + "\" of " + determineBeanName(xbean) + " must be a valid regular expression but compiling it failed with error: " + e.getMessage(), e);
			}
		}
	}
}
