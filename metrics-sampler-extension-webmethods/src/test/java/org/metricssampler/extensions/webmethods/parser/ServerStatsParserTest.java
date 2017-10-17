package org.metricssampler.extensions.webmethods.parser;

import org.junit.Test;
import org.metricssampler.reader.Metrics;

import java.io.IOException;
import java.text.ParseException;

import static org.metricssampler.extensions.webmethods.parser.MetricsAssert.assertMetric;

public class ServerStatsParserTest extends ParserTestBase {
	@Override
	protected AbstractFileParser createTestee() {
		return new ServerStatsParser(getConfig());
	}

	@Test
	public void parse() throws IOException,ParseException {
		final Metrics result = doParse();
		assertMetric(result, 1362057296000L, "ServerStats.ServiceErrors", "0");
		assertMetric(result, 1362057296000L, "ServerStats.TotalSessions.Current", "1");
		assertMetric(result, 1362057296000L, "ServerStats.TotalSessions.Peak", "3");
		assertMetric(result, 1362057296000L, "ServerStats.LicensedSessions.Current", "0");
		assertMetric(result, 1362057296000L, "ServerStats.LicensedSessions.Peak", "3");
		assertMetric(result, 1362057296000L, "ServerStats.LicensedSessions.Limit", "10000");
		assertMetric(result, 1362057296000L, "ServerStats.ServiceThreads.Current", "1");
		assertMetric(result, 1362057296000L, "ServerStats.ServiceThreads.Peak", "51");
		assertMetric(result, 1362057296000L, "ServerStats.Memory.Total", "1072431104");
		assertMetric(result, 1362057296000L, "ServerStats.Memory.Used", "302594048");
		assertMetric(result, 1362057296000L, "ServerStats.Memory.Free", "769837056");
	}
}
