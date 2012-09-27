package org.metricssampler.util;

import java.util.Collection;

public final class Preconditions {
	private Preconditions() {
	}
	
	public static void checkArgumentNotNullNorEmpty(final String argument, final String name) throws IllegalArgumentException {
		if (argument == null) {
			throw new IllegalArgumentException("Argument \"" + name + "\" may not be null");
		}
		if (argument.equals("")) {
			throw new IllegalArgumentException("Argument \"" + name + "\" may not be an empty string");
		}
	}
	
	public static void checkArgumentNotNull(final Object argument, final String name) throws IllegalArgumentException {
		if (argument == null) {
			throw new IllegalArgumentException("Argument \"" + name + "\" may not be null");
		}
	}
	
	public static void checkArgumentNotNullNorEmpty(final Collection<?> argument, final String name) throws IllegalArgumentException {
		if (argument == null) {
			throw new IllegalArgumentException("Argument \"" + name + "\" may not be null");
		}
		if (argument.isEmpty()) {
			throw new IllegalArgumentException("Argument \"" + name + "\" may not be empty");
		}
	}
	
	public static void checkArgument(final boolean condition, final String msg) throws IllegalArgumentException {
		if (!condition) {
			throw new IllegalArgumentException(msg);
		}
	}
}
