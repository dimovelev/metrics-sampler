package org.metricssampler.reader;

public class Metric {
    private final MetricName name;
    private final MetricValue value;

    public Metric(MetricName name, MetricValue value) {
        this.name = name;
        this.value = value;
    }

    public MetricName getName() {
        return name;
    }

    public MetricValue getValue() {
        return value;
    }
}
