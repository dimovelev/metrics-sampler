package org.metricssampler.extensions.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import org.metricssampler.reader.Metrics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.metricssampler.extensions.elasticsearch.ElasticSearchUtil.mapClusterHealthStatus;

public class ElasticSearchMetricValues {
    private final Metrics values;

    public ElasticSearchMetricValues(Metrics values) {
        this.values = values;
    }

    public void addClusterStats(JsonNode jsonNode) {
        final long timestamp = jsonNode.get("timestamp").asLong();
        final String clusterName = jsonNode.get("cluster_name").asText();
        final String status = jsonNode.get("status").asText().toLowerCase();
        final String prefix = "cluster." + clusterName;
        addMetric(timestamp, prefix + ".status", mapClusterHealthStatus(status));
        final List<String> categories = Arrays.asList("indices", "nodes");
        for (final String category : categories) {
            addJsonNodes(prefix + "." + category, timestamp, jsonNode.get(category));
        }
    }

    protected void addJsonNodes(final String prefix, final long timestamp, final JsonNode node) {
        if (node.isObject()) {
            final long newTimestamp = node.has("timestamp") ? node.get("timestamp").asLong() : timestamp;
            for(final Iterator<Map.Entry<String, JsonNode>> fields = node.fields(); fields.hasNext(); ) {
                final Map.Entry<String, JsonNode> field = fields.next();
                addJsonNodes(prefix + "." + field.getKey(), newTimestamp, field.getValue());
            }
        } else if (node.isNumber()) {
            addMetric(timestamp, prefix, node.asText());
        } else if (node.isBoolean()) {
            addMetric(timestamp, prefix, node.asBoolean() ? 1 : 0);
        }
    }

    protected void addMetric(final long timestamp, final String name, final Object value) {
        values.add(name, timestamp, value);
    }

    public void addNodeStats(JsonNode jsonNode) {
        final String clusterName = jsonNode.get("cluster_name").asText();
        final String prefix = "nodes." + clusterName;
        for(final JsonNode item : jsonNode.get("nodes")) {
            addSingleNodeStats(prefix, item);
        }
    }

    protected void addSingleNodeStats(final String clusterName, final JsonNode node) {
        final long timestamp = node.get("timestamp").asLong();
        final String nodeName = node.get("name").asText().replace(' ', '_');
        final String nodeHost = node.get("host").asText().replace('.', '_');
        final List<String> categories = Arrays.asList("indices", "os", "process", "jvm", "thread_pool", "network", "fs", "transport", "http", "breakers", "ingest");
        for(final String category : categories) {
            if (node.has(category)) {
                addJsonNodes(clusterName + "." + nodeName + "." + nodeHost + "." + category, timestamp, node.get(category));
            }
        }
    }

}
