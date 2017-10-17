package org.metricssampler.extensions.kafkamanager;

/**
 * A unique identifier for quering consumer related metrics from kafka-manager
 */
public class Consumer {
    private final String cluster;
    private final String group;
    private final String topic;
    private final String type;

    public Consumer(String cluster, String group, String topic, String type) {
        this.cluster = cluster;
        this.group = group;
        this.topic = topic;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Group " + group + " of type " + type + " consuming from topic " + topic + " in cluster " + cluster;
    }

    /**
     * @return the name of the kafka cluster
     */
    public String getCluster() {
        return cluster;
    }

    /**
     * @return the name of the consumer group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return the name of the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @return the type of consumer (e.g. ZK - zookeeper, KF - kafka)
     */
    public String getType() {
        return type;
    }
}
