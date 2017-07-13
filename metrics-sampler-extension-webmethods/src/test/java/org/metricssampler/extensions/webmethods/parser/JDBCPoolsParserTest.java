package org.metricssampler.extensions.webmethods.parser;

import org.junit.Test;
import org.metricssampler.reader.Metrics;

import java.io.IOException;
import java.text.ParseException;

import static org.metricssampler.extensions.webmethods.parser.MetricsAssert.assertMetric;

public class JDBCPoolsParserTest extends ParserTestBase {
	@Override
	protected AbstractFileParser createTestee() {
		return new JDBCPoolsParser(getConfig());
	}

	@Test
	public void parse() throws IOException,ParseException {
		final Metrics result = doParse();

		assertMetric(result, 1362057296000L, "JDBCPools.ProcessAudit.wMStorage.MinConnections", "0");
		assertMetric(result, 1362057296000L, "JDBCPools.ProcessAudit.wMStorage.MaxConnections", "20");
		assertMetric(result, 1362057296000L, "JDBCPools.ProcessAudit.wMStorage.TotalConnections", "0");
		assertMetric(result, 1362057296000L, "JDBCPools.ProcessAudit.wMStorage.AvailableConnections", "0");
		assertMetric(result, 1362057296000L, "JDBCPools.ISCoreAudit.wMStorage.MinConnections", "0");
		assertMetric(result, 1362057296000L, "JDBCPools.ISCoreAudit.wMStorage.MaxConnections", "20");
		assertMetric(result, 1362057296000L, "JDBCPools.ISCoreAudit.wMStorage.TotalConnections", "0");
		assertMetric(result, 1362057296000L, "JDBCPools.ISCoreAudit.wMStorage.AvailableConnections", "0");
	}
}
