package org.jmxsampler.config.loader.xbeans;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.jmxsampler.config.ConfigurationException;

public final class ValidationUtils {
	private ValidationUtils() {
	}

	public static void notEmpty(final String name, final String context, final String value) {
		if (value == null || value.equals("")) {
			throw new ConfigurationException("Attribute "+name+" of "+context+" is mandatory");
		}
	}

	public static void validUrl(final String name, final String context, final String value) {
		notEmpty(name, context, value);
		try {
			new URL(value);
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Attribute "+name+" of "+context+" is not a valid URL: "+e.getMessage());
		}
	}

	public static void notEmpty(final String name, final String context, final Collection<?> value) {
		if (value == null || value.isEmpty()) {
			throw new ConfigurationException("Attribute "+name+" of "+context+" is mandatory");
		}
	}

	public static void validPort(final String name, final String context, final int value) {
		if (value < 1 || value > 65535) {
			throw new ConfigurationException("Attribute "+name+" of "+context+" with value "+value+" is not a valid port in range [1,65535]");
		}
	}

	public static void greaterThanZero(final String name, final String context, final int value) {
		if (value < 1) {
			throw new ConfigurationException("Attribute "+name+" of "+context+" with value "+value+" is not a valid number higher than 0");
		}
	}
}
