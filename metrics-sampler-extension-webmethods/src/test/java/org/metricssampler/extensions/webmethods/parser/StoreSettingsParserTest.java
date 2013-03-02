package org.metricssampler.extensions.webmethods.parser;

import static org.metricssampler.extensions.webmethods.parser.MetricsAssert.assertMetric;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

public class StoreSettingsParserTest extends ParserTestBase {
	@Override
	protected AbstractFileParser createTestee() {
		return new StoreSettingsParser(getConfig());
	}

	@Test
	public void parse() throws IOException,ParseException {
		final Map<MetricName, MetricValue> result = doParse();
		assertMetric(result, 1362057296000L, "StoreSettings.DocumentStoreSettings.InitialStoreSizeMB", "25");
		assertMetric(result, 1362057296000L, "StoreSettings.DocumentStoreSettings.CapacityDocuments", "50");
		assertMetric(result, 1362057296000L, "StoreSettings.DocumentStoreSettings.RefillLevelDocuments", "20");
		assertMetric(result, 1362057296000L, "StoreSettings.DocumentStoreSettings.CurrentDocuments", "0");

		assertMetric(result, 1362057296000L, "StoreSettings.TriggerDocumentStore.InitialStoreSizeMB", "35");

		assertMetric(result, 1362057296000L, "StoreSettings.OutboundDocumentStore.CurrentDocumentsinOutboundStore", "0");
		assertMetric(result, 1362057296000L, "StoreSettings.OutboundDocumentStore.MaximumDocumentstoSendperTransaction", "25");

		assertMetric(result, 1362057296000L, "StoreSettings.XARecoveryStore.InitialStoreSizeMB", "10");
	}

}
