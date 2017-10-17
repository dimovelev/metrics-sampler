package org.metricssampler.extensions.webmethods.parser;

import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.Metrics;

import java.util.List;

public class TriggersInfoParser extends AbstractFileWithHeaderParser {
	public static final String ENTRY_RUNTIME_TRIGGERS_INFO = "runtime/TriggersInfo.txt";

	public TriggersInfoParser(final WebMethodsInputConfig config) {
		super(config, "TriggersInfo");
	}

	@Override
	protected void parseLine(final Metrics metrics, final List<String> lines, final int lineNum, final String line, final long timestamp) {
		if (line.length() > 0 && !line.startsWith("-")) {
			if (!line.startsWith(" ")) {
				lastSection = line.trim();
			} else {
				final int colIdx = line.indexOf(':');
				final String name = line.substring(0, colIdx);
				final String value = line.substring(colIdx + 2);
				final StringBuilder metricNameBuilder = new StringBuilder();
				if (name.startsWith(" ")) {
					if (lastSection != null) {
						metricNameBuilder.append(lastSection).append('.');
					}
				} else {
					lastSection = null;
				}
				metricNameBuilder.append(name);
				final String metricName = createMetricName(metricNameBuilder);
				final String actualValue = parseValue(metricName, value);
				metrics.add(metricName, timestamp, actualValue);
			}
		}
	}

	@Override
	public boolean canParseEntry(final String name) {
		return ENTRY_RUNTIME_TRIGGERS_INFO.equals(name);
	}

}
