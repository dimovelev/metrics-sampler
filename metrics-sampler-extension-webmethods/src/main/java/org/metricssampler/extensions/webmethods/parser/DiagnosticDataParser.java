package org.metricssampler.extensions.webmethods.parser;

import org.apache.commons.io.IOUtils;
import org.metricssampler.extensions.webmethods.WebMethodsInputConfig;
import org.metricssampler.reader.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;

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

	public Metrics parse(final Unzipper unzipper) throws ZipException, IOException, ParseException {
		final Metrics result = new Metrics();
		for (final String name : unzipper.getEntries()) {
			for (final AbstractFileParser parser : parsers) {
				if (parser.canParseEntry(name)) {
					result.addAll(parseZipEntry(unzipper, name, parser));
					break;
				}
			}
		}
		return result;
	}

	protected Metrics parseZipEntry(final Unzipper unzipper, final String name, final AbstractFileParser parser) throws IOException {
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
		return new Metrics();
	}
}
