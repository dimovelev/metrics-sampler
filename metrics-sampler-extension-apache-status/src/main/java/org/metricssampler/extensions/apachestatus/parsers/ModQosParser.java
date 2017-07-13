package org.metricssampler.extensions.apachestatus.parsers;

import org.metricssampler.reader.Metrics;

/**
 * Parse a status line from the mod_qos status module
 */
public class ModQosParser implements StatusLineParser {
	private static final String MOD_QOS_MARKER = "QS_";

	@Override
	public boolean parse(final String line, final Metrics metrics, final long timestamp) {
		if (!line.contains(MOD_QOS_MARKER)) {
			return false;
		}
		final String[] cols = line.split(";");
		final StringBuilder result = new StringBuilder();
		result.append("virtual=").append(cols[0])
			  .append(",host=").append(cols[1])
			  .append(",port=").append(cols[2]);
		final int colIdx = cols[3].indexOf(':');
		if (colIdx > 0) {
			result.append(",metric=").append(cols[3].substring(0, colIdx));
			final String value = cols[3].substring(colIdx+2);
			addValue(timestamp, metrics, result.toString(), value);
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
			addValue(timestamp, metrics, nameBase + ".limit", limit);
			addValue(timestamp, metrics, nameBase + ".current", current);
		}
		return true;
	}

	protected void addValue(final long timestamp, final Metrics metrics, final String name, final String value) {
		metrics.add(name, timestamp, value);
	}

}
