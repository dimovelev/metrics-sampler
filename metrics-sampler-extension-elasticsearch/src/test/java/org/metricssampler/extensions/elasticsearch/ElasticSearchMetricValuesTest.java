package org.metricssampler.extensions.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.metricssampler.reader.Metric;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.Metrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ElasticSearchMetricValuesTest {
    @Test
    public void addClusterStats() throws Exception {
        final Metrics values = new Metrics();
        final ElasticSearchMetricValues testee = new ElasticSearchMetricValues(values);

        testee.addClusterStats(loadJsonFromClasspath("cluster.json"));

        // sanity check for a few metrics
        final Optional<Metric> metric = values.get("cluster.elasticsearch.status");
        assertTrue(metric.isPresent());
        final MetricValue value = metric.get().getValue();
        assertEquals("cluster status wrong", Integer.valueOf(0), value.getValue());

        final Optional<Metric> metric2 = values.get("cluster.elasticsearch.nodes.count.data");
        final MetricValue dataNodeCount = metric2.get().getValue();
        assertEquals("data node count wrong", "1", dataNodeCount.getValue());
    }

    @Test
    public void addNodeStats() throws Exception {
        final Metrics values = new Metrics();
        final ElasticSearchMetricValues testee = new ElasticSearchMetricValues(values);

        testee.addNodeStats(loadJsonFromClasspath("nodes.json"));

        // sanity check for a few metrics
        final Optional<Metric> metric = values.get("nodes.elasticsearch.qw6Nixz.127_0_0_1.jvm.mem.heap_used_percent");
        assertTrue(metric.isPresent());
        MetricValue heapUsedPercent = metric.get().getValue();
        assertEquals("heap used percent wrong", "13", heapUsedPercent.getValue());
    }

    protected JsonNode loadJsonFromClasspath(String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try(InputStream is = getClass().getResourceAsStream(name)) {
            return objectMapper.readTree(is);
        }
    }

}