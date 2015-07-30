package org.metricssampler.extensions.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.metricssampler.reader.BaseHttpMetricsReader;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ElasticSearchMetricsReader extends BaseHttpMetricsReader<ElasticSearchInputConfig> {
    // JSON content-type according to RFC 4627
    protected final String JSON_CONTENT_TYPE = "application/json";

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final List<String> paths = asList("/_cluster/stats", "/_nodes/stats");

    public ElasticSearchMetricsReader(ElasticSearchInputConfig config) {
        super(config);
    }

    @Override
    protected List<String> getRequestPaths() {
        return paths;
    }

    @Override
    protected void processResponse(HttpUriRequest request, HttpResponse response) throws Exception {
        final HttpEntity entity = response.getEntity();
        if (entity.getContentType() != null && entity.getContentType().getValue().startsWith(JSON_CONTENT_TYPE)) {
            final JsonNode data = objectMapper.readTree(entity.getContent());
            processJsonResponse(request, data);
        } else {
            logger.warn("Unsupported content type: " + entity.getContentType());
        }
    }

    protected void processJsonResponse(HttpUriRequest request, JsonNode data) {
        final String uri = request.getRequestLine().getUri();
        if (uri.contains("_cluster")) {
            addClusterStats(data);
        } else if (uri.contains("_nodes")) {
            addNodeStats(data);
        } else {
            throw new IllegalArgumentException("Unsupported request path. This looks like a bug.");
        }
    }

    protected void addNodeStats(JsonNode jsonNode) {
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
        final List<String> categories = Arrays.asList("indices", "os", "process", "jvm", "thread_pool", "network", "fs", "transport", "http", "breakers");
        for(final String category : categories) {
            addJsonNodes(clusterName + "." + nodeName + "." + nodeHost + "." + category, timestamp, node.get(category));
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
        final SimpleMetricName metricName = new SimpleMetricName(name, null);
        final MetricValue metricValue = new MetricValue(timestamp, value);
        values.put(metricName, metricValue);
    }

    protected void addClusterStats(JsonNode jsonNode) {
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

    protected int mapClusterHealthStatus(String value) {
        switch(value) {
            case "green":
                return 0;
            case "yellow":
                return 1;
            case "red":
                return 2;
            default:
                return -1;
        }
    }

}
