package org.metricssampler.extensions.http;

import org.metricssampler.config.InputConfig;
import org.metricssampler.extensions.http.parsers.regexp.RegExpHttpResponseParserXBean;
import org.metricssampler.extensions.http.parsers.regexp.RegExpLineFormatXBean;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Adds support for fetching metrics from a URL. The response is parsed e.g. using regular expressions.
 */
public class HttpExtension extends AbstractExtension {
	@Override
	public Collection<Class<?>> getXBeans() {
		final List<Class<?>> result = new LinkedList<>();
		result.add(HttpInputXBean.class);
		result.add(RegExpHttpResponseParserXBean.class);
		result.add(RegExpLineFormatXBean.class);
		return result;
	}

	@Override
	public boolean supportsInput(final InputConfig config) {
		return config instanceof HttpInputConfig;
	}

	@Override
	protected MetricsReader doNewReader(final InputConfig config) {
		if (config instanceof HttpInputConfig) {
			return new HttpMetricsReader((HttpInputConfig) config);
		} else {
			throw new IllegalArgumentException("Unsupported reader config: " + config);
		}
	}
}
