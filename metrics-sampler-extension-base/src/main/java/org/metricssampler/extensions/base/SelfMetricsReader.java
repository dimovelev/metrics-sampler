package org.metricssampler.extensions.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SamplerThreadPool;
import org.metricssampler.service.GlobalRegistry;

public class SelfMetricsReader extends AbstractMetricsReader<SelfInputConfig> implements BulkMetricsReader{
	public SelfMetricsReader(final SelfInputConfig config) {
		super(config);
	}

	@Override
	public void open() {
		// nothing to do here
	}

	@Override
	public void close() {
		// nothing to do here
	}

	@Override
	public Iterable<MetricName> readNames() {
		return readAllMetrics().keySet();
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() {
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		final GlobalRegistry registry = GlobalRegistry.getInstance();
		final long timestamp = System.currentTimeMillis();
		for(final SamplerTask task : registry.getTasks()) {
			final SamplerStats stats = task.getStats();
			final String prefix = "samplers." + task.getName() + ".";
			result.put(new SimpleMetricName(prefix + "activeTime", "The number of seconds since the last activation of the sampler"), new MetricValue(timestamp, stats.getActiveTime()));
			result.put(new SimpleMetricName(prefix + "sampleSuccessCount", "The total number of successful samplings"), new MetricValue(timestamp, stats.getSampleSuccessCount()));
			result.put(new SimpleMetricName(prefix + "sampleFailureCount", "The total number of failed samplings due to unexpected exception"), new MetricValue(timestamp, stats.getSampleFailureCount()));
			result.put(new SimpleMetricName(prefix + "connectCount", "The total number of times the reader tried to connect to the input"), new MetricValue(timestamp, stats.getConnectCount()));
			result.put(new SimpleMetricName(prefix + "disconnectCount", "The total number of times the reader tried to disconnect from the input"), new MetricValue(timestamp, stats.getDisconnectCount()));
			result.put(new SimpleMetricName(prefix + "metricsCount", "The total number of metrics sampled the last time"), new MetricValue(timestamp, stats.getMetricsCount()));
			result.put(new SimpleMetricName(prefix + "sampleDuration", "The last sample duration in seconds"), new MetricValue(timestamp, stats.getSampleDuration()));
		}
		for (final SamplerThreadPool threadPool : registry.getSamplerThreadPools()) {
			final String prefix = "thread-pools." + threadPool.getName() + ".";
			final Map<String, Object> stats = threadPool.getStats();
			for (final Entry<String, Object> entry : stats.entrySet()) {
				result.put(new SimpleMetricName(prefix + entry.getKey(), ""), new MetricValue(timestamp, entry.getValue()));
			}
		}
		return result;
	}

}
