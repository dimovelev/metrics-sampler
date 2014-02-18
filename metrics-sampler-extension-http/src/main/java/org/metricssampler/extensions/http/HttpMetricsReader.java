package org.metricssampler.extensions.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.reader.BaseHttpMetricsReader;

public class HttpMetricsReader extends BaseHttpMetricsReader<HttpInputConfig> {
	private final HttpResponseParser parser;

	public HttpMetricsReader(final HttpInputConfig config) {
		super(config);
		parser = config.getParser();
	}

	@Override
	protected void processResponse(final HttpResponse response) throws IOException {
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
