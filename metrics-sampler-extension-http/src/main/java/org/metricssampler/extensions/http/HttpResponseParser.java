package org.metricssampler.extensions.http;

import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public interface HttpResponseParser {
	Map<MetricName, MetricValue> parse(final HttpResponse response, final HttpEntity entity, final InputStreamReader reader);
}
