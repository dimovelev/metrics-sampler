package org.metricssampler.extensions.webmethods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.metricssampler.extensions.webmethods.parser.DiagnosticDataParser;
import org.metricssampler.extensions.webmethods.parser.Unzipper;
import org.metricssampler.reader.HttpMetricsReader;

public class WebMethodsMetricsReader extends HttpMetricsReader<WebMethodsInputConfig> {
	private final DiagnosticDataParser parser;

	public WebMethodsMetricsReader(final WebMethodsInputConfig config) {
		super(config);
		parser = new DiagnosticDataParser(config);
	}

	@Override
	protected void processResponse(final HttpResponse response) throws IOException, ParseException {
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			final File file = File.createTempFile("diagnostic_data_", ".zip");
			file.deleteOnExit();
			try {
				FileWriter writer = null;

				InputStream content = null;
				try {
					writer = new FileWriter(file);
					content = entity.getContent();
					IOUtils.copy(content, writer);
				} finally {
					IOUtils.closeQuietly(content);
					IOUtils.closeQuietly(writer);
				}
				final Unzipper unzipper = new Unzipper(file, config.getMaxEntrySize());
				values = parser.parse(unzipper);
			} finally {
				FileUtils.deleteQuietly(file);
			}
		} else {
			values = Collections.emptyMap();
			logger.warn("Response was null. Response line: {}", response.getStatusLine());
		}
	}
}
