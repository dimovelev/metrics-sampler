package org.metricssampler.reader;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Metrics implements Iterable<Metric> {
    private final List<Metric> items;

    public Metrics(List<Metric> items) {
        this.items = items;
    }

    public Metrics() {
        this(new ArrayList<>());
    }

    public Metrics(Metric... metrics) {
        this(Arrays.asList(metrics));
    }

    public void clear() {
        items.clear();
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Iterator<Metric> iterator() {
        return items.iterator();
    }

    @Override
    public void forEach(Consumer<? super Metric> action) {
        items.forEach(action);
    }

    @Override
    public Spliterator<Metric> spliterator() {
        return items.spliterator();
    }

    public Set<MetricName> getNames() {
        return items.stream().map(Metric::getName).collect(Collectors.toSet());
    }

    public Optional<Metric> get(String name) {
        return items.stream().filter(e -> e.getName().getName().equals(name)).findFirst();
    }

    public List<Metric> getAll(String name) {
        return items.stream().filter(e -> e.getName().getName().equals(name)).collect(Collectors.toList());
    }

    public void add(Metric metric) {
        items.add(metric);
    }

    public void add(String name, String description, MetricValue value) {
        add(new Metric(new SimpleMetricName(name, description), value));
    }

    public void add(String name, MetricValue value) {
        add(new Metric(new SimpleMetricName(name, null), value));
    }
    public void add(MetricName name, MetricValue value) {
        add(new Metric(name, value));
    }

    public void add(MetricName name, long timestamp, Object value) {
        add(new Metric(name, new MetricValue(timestamp, value)));
    }

    public void add(String name, long timestamp, Object value) {
        add(new Metric(new SimpleMetricName(name, null), new MetricValue(timestamp, value)));
    }

    public void add(String name, String description, long timestamp, Object value) {
        add(new Metric(new SimpleMetricName(name, description), new MetricValue(timestamp, value)));
    }

    public void addAll(Metrics metrics) {
        items.addAll(metrics.items);
    }
}
