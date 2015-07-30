package org.metricssampler.extensions.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.metricssampler.reader.BaseHttpMetricsReader;

import java.io.InputStreamReader;
import java.util.Collections;

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
            values = Collections.emptyMap();
            logger.warn("Response was null. Response line: {}", response.getStatusLine());
        }
    }
}
