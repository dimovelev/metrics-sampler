package org.metricssampler.extensions.apachestatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

public class ModQosStatusLineParserTest {
	private ModQosStatusLineParser testee;
	private Map<MetricName, MetricValue> metrics;
	
	@Before
	public void setup() {
		testee = new ModQosStatusLineParser();
		metrics = new HashMap<MetricName, MetricValue>();
	}

	@Test
	public void parseAllConnections() {
		testee.parse("b;www.example.com;0;QS_AllConn: 1", metrics);

		assertEquals(1, metrics.size());
		final MetricValue value = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_AllConn", null));
		assertNotNull(value);
		assertEquals("1", value.getValue());
	}
	
	@Test
	public void parseLocationRequestLimits() {
		testee.parse("b;www.example.com;0;QS_LocRequestLimit;5[/path1]: 3", metrics);
		
		assertEquals(2, metrics.size());
		final MetricValue valueCurrent = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestLimit,path=/path1.current", null));
		assertNotNull(valueCurrent);
		assertEquals("3", valueCurrent.getValue());
		final MetricValue valueLimit = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestLimit,path=/path1.limit", null));
		assertNotNull(valueLimit);
		assertEquals("5", valueLimit.getValue());
	}
	
	@Test
	public void parseLocationRequestPerSecLimits() {
		testee.parse("b;www.example.com;0;QS_LocRequestPerSecLimit;5[/path2]: 2", metrics);
		
		assertEquals(2, metrics.size());
		final MetricValue valueCurrent = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestPerSecLimit,path=/path2.current", null));
		assertNotNull(valueCurrent);
		assertEquals("2", valueCurrent.getValue());
		final MetricValue valueLimit = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_LocRequestPerSecLimit,path=/path2.limit", null));
		assertNotNull(valueLimit);
		assertEquals("5", valueLimit.getValue());
	}
	
	@Test
	public void parseServerMaxConnections() {
		testee.parse("b;www.example.com;0;QS_SrvMaxConn;255[]: 8", metrics);
		
		assertEquals(2, metrics.size());
		final MetricValue valueCurrent = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConn.current", null));
		assertNotNull(valueCurrent);
		assertEquals("8", valueCurrent.getValue());
		final MetricValue valueLimit = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConn.limit", null));
		assertNotNull(valueLimit);
		assertEquals("255", valueLimit.getValue());
	}
	
	@Test
	public void parseServerMaxConnectionsClose() {
		testee.parse("b;www.example.com;0;QS_SrvMaxConnClose;130[]: 5", metrics);
		
		assertEquals(2, metrics.size());
		final MetricValue valueCurrent = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConnClose.current", null));
		assertNotNull(valueCurrent);
		assertEquals("5", valueCurrent.getValue());
		final MetricValue valueLimit = metrics.get(new SimpleMetricName("virtual=b,host=www.example.com,port=0,metric=QS_SrvMaxConnClose.limit", null));
		assertNotNull(valueLimit);
		assertEquals("130", valueLimit.getValue());
	}
}
