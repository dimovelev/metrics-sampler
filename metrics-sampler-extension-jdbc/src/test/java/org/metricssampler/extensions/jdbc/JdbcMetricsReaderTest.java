package org.metricssampler.extensions.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JdbcMetricsReaderTest {
    @Mock
    private JdbcConnectionPool jdbcConnectionPool;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet1;

    @Mock
    private ResultSetMetaData resultSetMetaData1;

    @Mock
    private ResultSet resultSet2;

    @Mock
    private ResultSetMetaData resultSetMetaData2;

    /**
     * Verify that all queries are executed and that the number of columns (2 and 3) are correctly interpreted.
     * @throws SQLException
     */
    @Test
    public void readAllMetrics() throws SQLException {
        JdbcInputConfig config = new JdbcInputConfig("test1", new HashMap<>(), "pool", Arrays.asList("select a, b from metrics", "select a, b, c from metrics"));
        // "select a, b, c from metrics"&
        JdbcMetricsReader testee = new JdbcMetricsReader(config, jdbcConnectionPool);

        when(jdbcConnectionPool.getConnection()).thenReturn(connection);
        testee.open();
        verify(jdbcConnectionPool).getConnection();

        // the first query only returns metric name and value
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("select a, b from metrics")).thenReturn(resultSet1);
        when(resultSet1.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet1.getMetaData()).thenReturn(resultSetMetaData1);
        when(resultSet1.getString(1)).thenReturn("m1.metric1").thenReturn("m1.metric2");
        when(resultSet1.getString(2)).thenReturn("1").thenReturn("2");
        when(resultSetMetaData1.getColumnCount()).thenReturn(2);

        // the second query returns metric name, value and timestamp => the same metric name can appear multiple times for different points in time
        when(statement.executeQuery("select a, b, c from metrics")).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet2.getMetaData()).thenReturn(resultSetMetaData2);
        when(resultSet2.getString(1)).thenReturn("m2.metric1").thenReturn("m2.metric2").thenReturn("m2.metric1");
        when(resultSet2.getString(2)).thenReturn("1").thenReturn("2").thenReturn("3");
        when(resultSet2.getLong(3)).thenReturn(1L).thenReturn(1L).thenReturn(2L);
        when(resultSetMetaData2.getColumnCount()).thenReturn(3);

        final Metrics result = testee.readAllMetrics();

        assertEquals(5, result.size());
        verifyMetric(result, "m1.metric1", "1");
        verifyMetric(result, "m1.metric2", "2");

        verifyMetric(result, "m2.metric1", new MetricValue(1L, "1"), new MetricValue(2L, "3"));
        verifyMetric(result, "m2.metric2", new MetricValue(1L, "2"));

        testee.close();
        verify(connection).close();
    }

    private void verifyMetric(Metrics result, String name, String expectedValue) {
        Optional<Metric> metric1 = result.get(name);
        assertTrue(metric1.isPresent());
        assertEquals(expectedValue, metric1.get().getValue().getValue());
    }

    private void verifyMetric(Metrics result, String name, MetricValue... expectedValues) {
        final Set<MetricValue> actualValues = result.getAll(name).stream().map(Metric::getValue).collect(Collectors.toSet());
        assertEquals("Less values found than expected", expectedValues.length, actualValues.size());
        for (MetricValue value : expectedValues) {
            assertTrue("Value " + value + " not found in " + actualValues, actualValues.contains(value));
        }
    }
}