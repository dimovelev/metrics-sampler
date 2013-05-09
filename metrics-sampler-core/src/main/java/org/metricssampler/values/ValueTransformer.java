package org.metricssampler.values;

public interface ValueTransformer {
	boolean matches(String metric);
	String transform(String value);
}
