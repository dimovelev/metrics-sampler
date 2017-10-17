package org.metricssampler.extensions.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.metricssampler.reader.BaseHttpMetricsReader;
import org.metricssampler.reader.Metrics;

import java.io.InputStreamReader;

public class HttpMetricsReader extends BaseHttpMetricsReader<HttpInputConfig> {
	protected final HttpResponseParser parser;

	public HttpMetricsReader(final HttpInputConfig config) {
		super(config);
		parser = config.getParser();
	}

    @Override
    protected void processResponse(HttpUriRequest request, HttpResponse response) throws Exception {
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            try(final InputStreamReader reader = streamEntity(entity)) {
                values = parser.parse(response, entity, reader);
            }
        } else {
            values = new Metrics();
            logger.warn("Response was null. Response line: {}", response.getStatusLine());
        }
    }
}
