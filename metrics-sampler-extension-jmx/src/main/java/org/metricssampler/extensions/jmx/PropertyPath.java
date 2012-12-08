package org.metricssampler.extensions.jmx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Denotes a path to a property of an attribute value.
 */
public class PropertyPath {
	private final List<PathSegment> segments;

	private PropertyPath(final List<PathSegment> segments) {
		this.segments = segments;
	}
	
	public static PropertyPath empty() {
		return new PropertyPath(Collections.<PathSegment>emptyList());
	}
	
	public static PropertyPath create(final PathSegment segment) {
		final List<PathSegment> segments = new ArrayList<PathSegment>(1);
		segments.add(segment);
		return new PropertyPath(segments);
	}
	
	public List<PathSegment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	public PropertyPath add(final PathSegment segment) {
		final List<PathSegment> newSegments = new ArrayList<PathSegment>(this.segments.size() + 1);
		newSegments.addAll(this.segments);
		newSegments.add(segment);
		return new PropertyPath(newSegments);
	}

	@Override
	public String toString() {
		if (segments.isEmpty()) {
			return "";
		}
		final StringBuilder result = new StringBuilder();
		for (final PathSegment segment : segments) {
			result.append(segment.toString());
		}
		if (result.charAt(0) == '.') {
			return "#" + result.substring(1);
		} else {
			return "#" + result.toString();
		}
	}
}
