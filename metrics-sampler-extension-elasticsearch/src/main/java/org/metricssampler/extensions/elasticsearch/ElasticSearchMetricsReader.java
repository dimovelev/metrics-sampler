package org.metricssampler.extensions.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.metricssampler.reader.BaseHttpMetricsReader;

import java.util.List;

import static java.util.Arrays.asList;

public class ElasticSearchMetricsReader extends BaseHttpMetricsReader<ElasticSearchInputConfig> {
    // JSON content-type according to RFC 4627
    protected final String JSON_CONTENT_TYPE = "application/json";

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final List<String> paths = asList("/_cluster/stats", "/_nodes/stats");

    public ElasticSearchMetricsReader(ElasticSearchInputConfig config) {
        super(config);
    }

    protected ElasticSearchMetricValues elasticSearchMetricValues;

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

    @Override
    protected List<HttpUriRequest> setupRequests() {
        elasticSearchMetricValues = new ElasticSearchMetricValues(values);
        return super.setupRequests();
    }

    protected void processJsonResponse(HttpUriRequest request, JsonNode data) {
        final String uri = request.getRequestLine().getUri();
        if (uri.contains("_cluster")) {
            elasticSearchMetricValues.addClusterStats(data);
        } else if (uri.contains("_nodes")) {
            elasticSearchMetricValues.addNodeStats(data);
        } else {
            throw new IllegalArgumentException("Unsupported request path. This looks like a bug.");
        }
    }

}
