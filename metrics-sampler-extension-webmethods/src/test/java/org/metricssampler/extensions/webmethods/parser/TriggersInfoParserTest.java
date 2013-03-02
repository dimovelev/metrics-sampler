package org.metricssampler.extensions.webmethods.parser;

import static org.metricssampler.extensions.webmethods.parser.MetricsAssert.assertMetric;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class TriggersInfoParserTest extends ParserTestBase {
	@Override
	protected AbstractFileParser createTestee() {
		return new TriggersInfoParser(getConfig());
	}

	@Test
	public void parse() throws IOException,ParseException {
		final Map<MetricName, MetricValue> result = doParse();
		assertMetric(result, 1362057296000L, "TriggersInfo.ANON.Application.Communication.trgSendCommunication.CurrentThreadCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.ANON.Application.Communication.trgSendCommunication.PersistedQueueCount", "0");

		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.CurrentThreadCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.PersistedQueueCount", "0");
		assertMetric(result, 1362057296000L, "TriggersInfo.Processes.MemberExport.trgLogMemberExport.VolatileQueueCount", "0");
	}

}
