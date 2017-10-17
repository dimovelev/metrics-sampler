package org.metricssampler.extensions.kafkamanager;

/**
 * Metrics about a consumer queried from kafka-manager
 */
public class ConsumerMetrics {
    private final Consumer consumer;
    private final long totalLag;
    private final int percentageCovered;

    public ConsumerMetrics(Consumer consumer, long totalLag, int percentageCovered) {
        this.consumer = consumer;
        this.totalLag = totalLag;
        this.percentageCovered = percentageCovered;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public long getTotalLag() {
        return totalLag;
    }

    public int getPercentageCovered() {
        return percentageCovered;
    }
}
