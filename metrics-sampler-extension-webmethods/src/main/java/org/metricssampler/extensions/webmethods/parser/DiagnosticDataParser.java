package org.metricssampler.extensions.webmethods.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import org.apache.commons.io.IOUtils;
import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the diagnostic data ZIP file downloaded over HTTP from webmethods
 */
public final class DiagnosticDataParser {
	private static final Logger logger = LoggerFactory.getLogger(DiagnosticDataParser.class);
	private final List<AbstractFileParser> parsers;

	public DiagnosticDataParser(final WebMethodsInputConfig config) {
		parsers = Arrays.<AbstractFileParser>asList(
				new JDBCPoolsParser(config),
				new ServerStatsParser(config),
				new StoreSettingsParser(config),
				new TriggersInfoParser(config));
	}

	public Map<MetricName, MetricValue> parse(final Unzipper unzipper) throws ZipException, IOException, ParseException {
		final Map<MetricName, MetricValue> result = new HashMap<>();
		for (final String name : unzipper.getEntries()) {
			for (final AbstractFileParser parser : parsers) {
				if (parser.canParseEntry(name)) {
					result.putAll(parseZipEntry(unzipper, name, parser));
					break;
				}
			}
		}
		return result;
	}

	protected Map<MetricName, MetricValue> parseZipEntry(final Unzipper unzipper, final String name, final AbstractFileParser parser) throws IOException {
		logger.debug("Parsing \"{}\" with \"{}\"", name, parser.getClass().getSimpleName());
		final InputStream stream = unzipper.unzip(name);
		if (stream != null) {
			try {
				return parser.parse(stream);
			} catch (final ParseException e) {
				logger.warn("Failed to parse ZIP entry \"{}\": {}", name, e.getMessage());
			} catch (final ZipException e) {
				logger.warn("Failed to unzip entry \"{}\": {}", name, e.getMessage());
			} finally {
				IOUtils.closeQuietly(stream);
			}
		} else {
			logger.warn("ZIP does not contain entry \"{}\" or the entry is too large to parse", name);
		}
		return Collections.emptyMap();
	}
}
