package org.jmxsampler.extensions.modqos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jmxsampler.extensions.modqos.ModQosReaderConfig.AuthenticationType;
import org.jmxsampler.reader.AbstractMetricsReader;
import org.jmxsampler.reader.MetricReadException;
import org.jmxsampler.reader.SourceMetricMetaData;

public class ModQosMetricsReader extends AbstractMetricsReader {
	private final ModQosReaderConfig config;
	private List<String> data;
	private Collection<SourceMetricMetaData> metadata;
	private Map<SourceMetricMetaData, String> values;

	public ModQosMetricsReader(final ModQosReaderConfig config) {
		this.config = config;
	}

	@Override
	public void open() throws MetricReadException {
		try {
			final URLConnection urlConnection = config.getUrl().openConnection();
			applyAuthentication(urlConnection);
			final InputStream is = (InputStream) urlConnection.getContent();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			data = new LinkedList<String>();
			while ( (line = reader.readLine()) != null) {
				data.add(line);
			}
			reader.close();
			parseData();
			notifyOnConnected();
		} catch (final IOException e) {
			throw new MetricReadException("Failed to open connection", e);
		}
	}

	private void applyAuthentication(final URLConnection urlConnection) throws UnsupportedEncodingException {
		if (config.getAuthType() == AuthenticationType.BASIC) {
			final String userpass = config.getUsername() + ":" + config.getPassword();
			final String basicAuthHeader = "Basic " + Base64.encodeBase64String(userpass.getBytes("ASCII"));
			urlConnection.setRequestProperty ("Authorization", basicAuthHeader);
		}
	}

	private void parseData() {
		metadata = new ArrayList<SourceMetricMetaData>(data.size());
		values = new HashMap<SourceMetricMetaData, String>();
		for (final String line : data) {
			parseModQosLine(line);
		}
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
			addMetric(result.toString(), value);
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
			addMetric(nameBase + ".limit", limit);
			addMetric(nameBase + ".current", current);
		}
	}

	protected void addMetric(final String name, final String value) {
		final SourceMetricMetaData metric = new SourceMetricMetaData(name, null);
		metadata.add(metric);
		values.put(metric, value);
	}

	@Override
	public void close() {
		// the connection is closed by open()
	}

	@Override
	public Collection<SourceMetricMetaData> getMetaData() throws MetricReadException {
		return metadata;
	}

	@Override
	public Map<String, String> getTransformationContext() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("reader.name", config.getName());
		return result;
	}

	@Override
	public Object readMetric(final SourceMetricMetaData metric) throws MetricReadException {
		return values.get(metric);
	}
}
