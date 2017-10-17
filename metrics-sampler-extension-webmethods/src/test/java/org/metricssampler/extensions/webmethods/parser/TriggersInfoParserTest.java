package org.metricssampler.extensions.webmethods.parser;

import org.junit.Test;
import org.metricssampler.reader.Metrics;

import java.io.IOException;
import java.text.ParseException;

import static org.metricssampler.extensions.webmethods.parser.MetricsAssert.assertMetric;

public class TriggersInfoParserTest extends ParserTestBase {
	@Override
	protected AbstractFileParser createTestee() {
		return new TriggersInfoParser(getConfig());
	}

	@Test
	public void parse() throws IOException,ParseException {
		final Metrics result = doParse();

		assertMetric(result, 1362057296000L, "TriggersInfo.ANON.Application.Communication.trgSendCommunication.CurrentThreadCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.ANON.Application.Communication.trgSendCommunication.PersistedQueueCount", "0");

		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.CurrentThreadCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.PersistedQueueCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.VolatileQueueCount", "0");
	}

}
