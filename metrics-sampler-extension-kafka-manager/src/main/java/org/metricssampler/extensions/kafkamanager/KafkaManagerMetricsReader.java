package org.metricssampler.extensions.kafkamanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;
import org.metricssampler.reader.BaseHttpMetricsReader;
import org.metricssampler.reader.Metrics;
import org.metricssampler.resources.SamplerThreadPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

public class KafkaManagerMetricsReader extends BaseHttpMetricsReader<KafkaManagerInputConfig> {
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final SamplerThreadPool threadPool;

    public KafkaManagerMetricsReader(KafkaManagerInputConfig config, SamplerThreadPool threadPool) {
        super(config);
        this.threadPool = threadPool;
    }

    @Override
    protected void fetchOverHttp(final HttpClient httpClient, HttpContext httpContext) throws Exception {
        values = new Metrics();
        final List<String> clusters = fetchActiveClusterNames(httpClient);
        final List<Consumer> consumers = new ArrayList<>();
        for (final String cluster : clusters) {
            final List<Consumer> clusterConsumers = filterConsumers(fetchConsumers(httpClient, cluster));
            consumers.addAll(clusterConsumers);
        }

        List<Future<ConsumerMetrics>> results = consumers.stream().map(c -> threadPool.submit(() -> fetchConsumerDetails(httpClient, c))).collect(Collectors.toList());
        for (Future<ConsumerMetrics> result : results) {
            final ConsumerMetrics consumerMetrics = result.get();
            if (consumerMetrics != null) {
                final String prefix = "clusters." + consumerMetrics.getConsumer().getCluster() + ".topics." + consumerMetrics.getConsumer().getTopic() + ".consumers." + consumerMetrics.getConsumer().getGroup() + ".";
                final long timestamp = System.currentTimeMillis();
                values.add(prefix + "totalLag", "The total lag of the given consumer", timestamp, consumerMetrics.getTotalLag());
                values.add(prefix + "percentageCovered", "Percentage of the partitions that have an owner", timestamp, consumerMetrics.getPercentageCovered());
            }
        }
    }

    protected List<Consumer> filterConsumers(List<Consumer> consumers) {
        return consumers.stream().filter(c -> !c.getGroup().startsWith("console-consumer-")).collect(Collectors.toList());
    }

    protected List<String> fetchActiveClusterNames(HttpClient httpClient) throws IOException {
        final List<String> result = new ArrayList<>();

        final JsonNode data = fetchJson("api/status/clusters");

        if (data != null) {
            for (final JsonNode cluster : data.get("clusters").get("active")) {
                result.add(cluster.get("name").asText());
            }
        }

        return result;
    }

    protected List<Consumer> fetchConsumers(HttpClient httpClient, String cluster) throws IOException {
        final List<Consumer> result = new ArrayList<>();

        final JsonNode data = fetchJson("api/status/" + cluster + "/consumersSummary");

        if (data != null) {
            for (JsonNode consumerNode : data.get("consumers")) {
                final String groupName = consumerNode.get("name").asText();
                final String type = consumerNode.get("type").asText();
                for (JsonNode topicNode : consumerNode.get("topics")) {
                    result.add(new Consumer(cluster, groupName, topicNode.asText(), type));
                }
            }
        }

        return result;
    }

    protected JsonNode fetchJson(final String path) throws IOException {
        final HttpGet request = new HttpGet(config.getUrl().toString() + path);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

        logger.debug("Fetching data from [{}]", request.getURI());

        final HttpResponse response = httpClient.execute(request);
        if (httpStatusIsSuccess(response.getStatusLine().getStatusCode())) {
            final HttpEntity entity = response.getEntity();
            if (isJsonEntity(entity.getContentType())) {
                return objectMapper.readTree(entity.getContent());
            } else {
                logger.warn("Unsupported content type received from [{}]: [{}]", request.getURI(), entity.getContentType());
            }
        } else {
            logger.warn("Kafka-manager returned unexpected HTTP status line [{}]", response.getStatusLine());
        }
        return null;
    }

    private boolean isJsonEntity(Header contentType) {
        // kafka-manager returns text/plain as content type even when JSON is returned
        return contentType != null && (contentType.getValue().startsWith(APPLICATION_JSON.getMimeType()) || contentType.getValue().startsWith(TEXT_PLAIN.getMimeType()));
    }

    protected boolean httpStatusIsSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    protected ConsumerMetrics fetchConsumerDetails(HttpClient httpClient, Consumer consumer) throws IOException {
        final JsonNode data = fetchJson("api/status/" + consumer.getCluster() + "/" + consumer.getGroup() + "/" + consumer.getTopic() + "/" + consumer.getType() + "/topicSummary");

        if (data != null) {
            return new ConsumerMetrics(consumer, data.get("totalLag").asLong(), data.get("percentageCovered").asInt());
        }

        return null;
    }

    @Override
    protected void processResponse(HttpUriRequest request, HttpResponse response) throws Exception {
        // not used
    }
}
