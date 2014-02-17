package org.metricssampler.reader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

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
import org.metricssampler.config.BaseHttpInputConfig;
import org.metricssampler.service.ApplicationInfo;
import org.metricssampler.util.VariableUtils;

public abstract class BaseHttpMetricsReader<T extends BaseHttpInputConfig> extends AbstractMetricsReader<T> implements BulkMetricsReader {
	private final DefaultHttpClient httpClient;
	private final HttpGet httpRequest;
	private final HttpContext httpContext;
	protected Map<MetricName, MetricValue> values;

	public BaseHttpMetricsReader(final T config) {
		super(config);
		httpClient = setupClient();
		httpRequest = setupRequest();
		httpContext = setupContext();
	}

	protected DefaultHttpClient setupClient() {
		final DefaultHttpClient result = new DefaultHttpClient();
		if (config.getUsername() != null) {
			result.getCredentialsProvider().setCredentials(
					AuthScope.ANY,
	                new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
		}
		return result;
	}

	protected HttpGet setupRequest() {
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

	protected HttpContext setupContext() {
	    final BasicHttpContext result = new BasicHttpContext();
		if (config.isPreemptiveAuthEnabled()) {
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
		} catch (final Exception e) {
			throw new OpenMetricsReaderException(e);
		}
		final long end = System.currentTimeMillis();
		timingsLogger.debug("Discovered {} metrics in {} ms", values.size(), end - start);
	}

	protected abstract void processResponse(HttpResponse response) throws Exception;

	protected Charset parseCharset(final HttpEntity entity) {
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

	protected InputStreamReader streamEntity(final HttpEntity entity) throws IOException {
		final Charset charset = parseCharset(entity);
		final InputStreamReader reader = new InputStreamReader(entity.getContent(), charset);
		return reader;
	}

}