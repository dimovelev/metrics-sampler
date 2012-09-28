package org.metricssampler.extensions.modqos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.reader.SimpleMetricName;

public class ModQosMetricsReader extends AbstractMetricsReader<ModQosInputConfig> implements BulkMetricsReader {
	private Map<MetricName, MetricValue> values;
	private final DefaultHttpClient httpClient;
	private final HttpGet httpRequest;

	public ModQosMetricsReader(final ModQosInputConfig config) {
		super(config);
		httpClient = setupClient();
		httpRequest = setupRequest();
	}

	private DefaultHttpClient setupClient() {
		final DefaultHttpClient result = new DefaultHttpClient();
		if (config.getUsername() != null) {
			result.getCredentialsProvider().setCredentials(
					AuthScope.ANY,
	                new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
		}
		return result;
	}

	private HttpGet setupRequest() {
		try {
			final HttpGet result = new HttpGet(config.getUrl().toURI());
			result.setHeader("User-Agent", "metrics-sampler mod_qos");
			for (final Entry<String, String> header : config.getHeaders().entrySet()) {
				result.setHeader(header.getKey(), header.getValue());
			}
			return result;
		} catch (final URISyntaxException e) {
			throw new ConfigurationException("Failed to convert URL to URI", e);
		}
	}

	@Override
	protected void defineCustomVariables(final Map<String, Object> variables) {
		variables.put("reader.host", config.getUrl().getHost());
	}

	@Override
	public void open() throws MetricReadException {
		final long start = System.currentTimeMillis();
		try {
	            final HttpResponse response = httpClient.execute(httpRequest);
	            processResponse(response);
		} catch (final IOException e) {
			throw new OpenMetricsReaderException(e);
		}
		final long end = System.currentTimeMillis();
		timingsLogger.debug("Discovered {} metrics in {} ms", values.size(), end - start);
	}

	private void processResponse(final HttpResponse response) throws IOException {
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			final Charset charset = parseCharset(entity);
		    final InputStreamReader reader = new InputStreamReader(entity.getContent(), charset);
		    try {
				final LineIterator lines = new LineIterator(reader);
				try {
					values = new HashMap<MetricName, MetricValue>();
					while (lines.hasNext()) {
						final String line = lines.next();
						parseModQosLine(line);
					}
				} finally {
					lines.close();
				}
		    } finally {
		    	IOUtils.closeQuietly(reader);
		    }
		} else {
			values = Collections.emptyMap();
			logger.warn("Response was null. Response line: {}", response.getStatusLine());
		}
	}

	private Charset parseCharset(final HttpEntity entity) {
		try {
			final ContentType contentType = ContentType.getOrDefault(entity);
		    if (contentType != null && contentType.getCharset() != null) {
		    	return contentType.getCharset();
		    }
		} catch (final ParseException e) {
			logger.warn("Failed to parse content type", e);
		}
		return Charset.defaultCharset();
	}

	protected void parseModQosLine(final String line) {
		final String[] cols = line.split(";");
		final StringBuilder result = new StringBuilder();
		result.append("virtual=").append(cols[0])
			  .append(",host=").append(cols[1])
			  .append(",port=").append(cols[2]);
		final int colIdx = cols[3].indexOf(':');
		if (colIdx > 0) {
			result.append(",metric=").append(cols[3].substring(0, colIdx));
			final String value = cols[3].substring(colIdx+2);
			addValue(result.toString(), value);
		} else {
			result.append(",metric=").append(cols[3]);
			final int pathStartIdx = cols[4].indexOf('[');
			final int colonIdx = cols[4].indexOf(':', pathStartIdx);
			final String path = cols[4].substring(pathStartIdx+1, colonIdx-1);
			if (!path.isEmpty()) {
				result.append(",path=").append(path);
			}
			final String limit = cols[4].substring(0, pathStartIdx);
			final String current = cols[4].substring(colonIdx+2);
			final String nameBase = result.toString();
			addValue(nameBase + ".limit", limit);
			addValue(nameBase + ".current", current);
		}
	}

	protected void addValue(final String name, final String value) {
		final SimpleMetricName metric = new SimpleMetricName(name, null);
		values.put(metric, new MetricValue(System.currentTimeMillis(), value));
	}

	@Override
	public void close() {
		// the connection is closed already by open()
	}

	@Override
	public Iterable<MetricName> readNames() throws MetricReadException {
		return values.keySet();
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		return Collections.unmodifiableMap(values);
	}
}
