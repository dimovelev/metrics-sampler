package org.metricssampler.extensions.base;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.Metrics;
import org.metricssampler.resources.SamplerStats;
import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.GlobalRegistry;

import java.util.Map;
import java.util.Map.Entry;

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
	public Metrics readAllMetrics() {
		final Metrics result = new Metrics();
		final GlobalRegistry registry = GlobalRegistry.getInstance();
		final long timestamp = System.currentTimeMillis();
		for(final SamplerTask task : registry.getTasks()) {
			final SamplerStats stats = task.getStats();
			final String prefix = "samplers." + task.getName() + ".";
			result.add(prefix + "activeTime", "The number of seconds since the last activation of the sampler", timestamp, stats.getActiveTime());
			result.add(prefix + "sampleSuccessCount", "The total number of successful samplings", timestamp, stats.getSampleSuccessCount());
			result.add(prefix + "sampleFailureCount", "The total number of failed samplings due to unexpected exception", timestamp, stats.getSampleFailureCount());
			result.add(prefix + "connectCount", "The total number of times the reader tried to connect to the input", timestamp, stats.getConnectCount());
			result.add(prefix + "disconnectCount", "The total number of times the reader tried to disconnect from the input", timestamp, stats.getDisconnectCount());
			result.add(prefix + "metricsCount", "The total number of metrics sampled the last time", timestamp, stats.getMetricsCount());
			result.add(prefix + "sampleDuration", "The last sample duration in seconds", timestamp, stats.getSampleDuration());
		}

		for (final SharedResource sharedResource : registry.getSharedResources()) {
			final Map<String, Object> stats = sharedResource.getStats();
			for (final Entry<String, Object> entry : stats.entrySet()) {
				result.add(entry.getKey(), timestamp, entry.getValue());
			}
		}
		return result;
	}

}
