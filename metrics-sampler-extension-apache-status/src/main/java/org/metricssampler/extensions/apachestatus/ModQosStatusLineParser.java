package org.metricssampler.extensions.apachestatus;

import java.util.Map;

import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public class ModQosStatusLineParser {
	public void parse(final String line, final Map<MetricName, MetricValue> metrics) {
		final String[] cols = line.split(";");
		final StringBuilder result = new StringBuilder();
		result.append("virtual=").append(cols[0])
			  .append(",host=").append(cols[1])
			  .append(",port=").append(cols[2]);
		final int colIdx = cols[3].indexOf(':');
		if (colIdx > 0) {
			result.append(",metric=").append(cols[3].substring(0, colIdx));
			final String value = cols[3].substring(colIdx+2);
			addValue(metrics, result.toString(), value);
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
			addValue(metrics, nameBase + ".limit", limit);
			addValue(metrics, nameBase + ".current", current);
		}
	}

	protected void addValue(final Map<MetricName, MetricValue> metrics, final String name, final String value) {
		final SimpleMetricName metric = new SimpleMetricName(name, null);
		metrics.put(metric, new MetricValue(System.currentTimeMillis(), value));
	}
	
}
