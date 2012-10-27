package org.metricssampler.extensions.apachestatus;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.extensions.apachestatus.parsers.GenericLineParser;
import org.metricssampler.extensions.apachestatus.parsers.ModQosParser;
import org.metricssampler.extensions.apachestatus.parsers.ScoreboardParser;
import org.metricssampler.extensions.apachestatus.parsers.StatusLineParser;
import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.service.ApplicationInfo;
import org.metricssampler.util.VariableUtils;

public class ApacheStatusMetricsReader extends AbstractMetricsReader<ApacheStatusInputConfig> implements BulkMetricsReader {
	private Map<MetricName, MetricValue> values;
	private final List<StatusLineParser> lineParsers = Arrays.asList(new ModQosParser(), new ScoreboardParser(), new GenericLineParser());
	private final DefaultHttpClient httpClient;
	private final HttpGet httpRequest;
	private final HttpContext httpContext;

	public ApacheStatusMetricsReader(final ApacheStatusInputConfig config) {
		super(config);
		httpClient = setupClient();
		httpRequest = setupRequest();
		httpContext = setupContext();
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
			result.setHeader("User-Agent", "metrics-sampler apache-status v" + ApplicationInfo.getInstance().getVersion());
			for (final Entry<String, String> header : config.getHeaders().entrySet()) {
				result.setHeader(header.getKey(), header.getValue());
			}
			return result;
		} catch (final URISyntaxException e) {
			throw new ConfigurationException("Failed to convert URL to URI", e);
		}
	}

	private HttpContext setupContext() {
        final BasicHttpContext result = new BasicHttpContext();
		if (config.isPreemtiveAuthEnabled()) {
	        final AuthCache authCache = new BasicAuthCache();
	        final BasicScheme basicAuth = new BasicScheme();
	        authCache.put(new HttpHost(config.getUrl().getHost(), config.getUrl().getPort()), basicAuth);
	        result.setAttribute(ClientContext.AUTH_CACHE, authCache);
		}
		return result;
	}

	@Override
	protected void defineCustomVariables(final Map<String, Object> variables) {
		VariableUtils.addHostVariables(variables, "input", config.getUrl().getHost());
	}

	@Override
	public void open() throws MetricReadException {
		final long start = System.currentTimeMillis();
		try {
	            final HttpResponse response = httpClient.execute(httpRequest, httpContext);
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
					final long timestamp = System.currentTimeMillis();
					while (lines.hasNext()) {
						final String line = lines.next();
						parseLine(line, timestamp);
					}
				} finally {
					lines.close();
				}
		    } finally {
		    	closeQuietly(reader);
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

	protected void parseLine(final String line, final long timestamp) {
		boolean parsed = false;
		for (final StatusLineParser lineParser : lineParsers) {
			parsed = parsed || lineParser.parse(line, values, timestamp);
			if (parsed) {
				break;
			}
		}
		if (!parsed) {
			logger.debug("Ignoring response line \"{}\" as I do not know how to parse it", line);
		}
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
