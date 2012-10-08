package org.metricssampler.extensions.apachestatus.parsers;

import org.junit.Test;

public class GenericLineParserTest extends StatusLineParserTestBase {

	@Override
	protected StatusLineParser createTestee() {
		return new GenericLineParser();
	}

	@Test
	public void parseTotalAccesses() {
		parseSuccess("Total Accesses: 1006508");

		assertMetric("Total_Accesses", "1006508");
	}

	@Test
	public void parseTotalkBytes() {
		parseSuccess("Total kBytes: 4076182");

		assertMetric("Total_kBytes", "4076182");
	}

	@Test
	public void parseReqPerSec() {
		parseSuccess("ReqPerSec: 1.99871");

		assertMetric("ReqPerSec", "1.99871");
	}
}
