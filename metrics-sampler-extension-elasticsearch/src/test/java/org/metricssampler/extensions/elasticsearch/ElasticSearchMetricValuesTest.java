package org.metricssampler.extensions.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ElasticSearchMetricValuesTest {
    @Test
    public void addClusterStats() throws Exception {
        final HashMap<MetricName, MetricValue> values = new HashMap<>();
        final ElasticSearchMetricValues testee = new ElasticSearchMetricValues(values);

        testee.addClusterStats(loadJsonFromClasspath("cluster.json"));

        // sanity check for a few metrics
        final MetricValue value = values.get(new SimpleMetricName("cluster.elasticsearch.status", ""));
        assertEquals("cluster status wrong", Integer.valueOf(0), value.getValue());

        final MetricValue dataNodeCount = values.get(new SimpleMetricName("cluster.elasticsearch.nodes.count.data", ""));
        assertEquals("data node count wrong", "1", dataNodeCount.getValue());
    }

    @Test
    public void addNodeStats() throws Exception {
        final HashMap<MetricName, MetricValue> values = new HashMap<>();
        final ElasticSearchMetricValues testee = new ElasticSearchMetricValues(values);

        testee.addNodeStats(loadJsonFromClasspath("nodes.json"));

        // sanity check for a few metrics
        final MetricValue heapUsedPercent = values.get(new SimpleMetricName("nodes.elasticsearch.qw6Nixz.127_0_0_1.jvm.mem.heap_used_percent", ""));
        assertEquals("heap used percent wrong", "13", heapUsedPercent.getValue());
    }

    protected JsonNode loadJsonFromClasspath(String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try(InputStream is = getClass().getResourceAsStream(name)) {
            return objectMapper.readTree(is);
        }
    }

}