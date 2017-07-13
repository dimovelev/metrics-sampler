package org.metricssampler.extensions.apachestatus.parsers;

import org.junit.Test;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModQosParserTest extends StatusLineParserTestBase {
	@Override
	protected StatusLineParser createTestee() {
		return new ModQosParser();
	}

	@Test
	public void parseWrongLine() {
		parseFailure("whatever: 1");
	}

	@Test
	public void parseAllConnections() {
		parseSuccess("b;www.example.com;0;QS_AllConn: 1");

		assertEquals(1, metrics.size());
		final Optional<Metric> metric = metrics.get("virtual=b,host=www.example.com,port=0,metric=QS_AllConn");
		assertTrue(metric.isPresent());
		MetricValue value = metric.get().getValue();
		assertEquals("1", value.getValue());
		assertEquals(timestamp, value.getTimestamp());
	}

	@Test
	public void parseLocationRequestLimits() {
		parseSuccess("b;www.example.com;0;QS_LocRequestLimit;5[/path1]: 3");

		assertEquals(2, metrics.size());
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestLimit,path=/path1.current", "3");
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestLimit,path=/path1.limit", "5");
	}

	@Test
	public void parseLocationRequestPerSecLimits() {
		parseSuccess("b;www.example.com;0;QS_LocRequestPerSecLimit;5[/path2]: 2");

		assertEquals(2, metrics.size());
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestPerSecLimit,path=/path2.current", "2");
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestPerSecLimit,path=/path2.limit", "5");
	}

	@Test
	public void parseServerMaxConnections() {
		parseSuccess("b;www.example.com;0;QS_SrvMaxConn;255[]: 8");

		assertEquals(2, metrics.size());
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConn.current", "8");
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConn.limit", "255");
	}

	@Test
	public void parseServerMaxConnectionsClose() {
		parseSuccess("b;www.example.com;0;QS_SrvMaxConnClose;130[]: 5");

		assertEquals(2, metrics.size());
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConnClose.current", "5");
		assertMetric("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConnClose.limit", "130");
	}
}
