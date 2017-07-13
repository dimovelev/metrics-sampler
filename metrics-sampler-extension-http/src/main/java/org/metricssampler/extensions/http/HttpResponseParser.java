package org.metricssampler.extensions.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.reader.Metrics;

import java.io.InputStreamReader;

public interface HttpResponseParser {
	Metrics parse(final HttpResponse response, final HttpEntity entity, final InputStreamReader reader);
}
