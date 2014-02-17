package org.metricssampler.extensions.http;

/**
 * Base class for XBeans defining a parser for the HTTP responses.
 */
public abstract class HttpResponseParserXBean {
	protected abstract void validate();
	protected abstract HttpResponseParser createParser();
}
