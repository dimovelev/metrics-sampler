package org.metricssampler.extensions.jmx;

import javax.management.openmbean.CompositeData;

public class PropertyPathSegment extends PathSegment {
	private final String name;

	public PropertyPathSegment(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "." + name;
	}

	@Override
	public Object getValue(final Object result) {
		return ((CompositeData) result).get(name);
	}
}