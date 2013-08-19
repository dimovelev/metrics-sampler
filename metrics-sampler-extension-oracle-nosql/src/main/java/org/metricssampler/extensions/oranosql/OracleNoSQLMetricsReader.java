package org.metricssampler.extensions.oranosql;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.kv.KVStore;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.measurement.LatencyInfo;
import oracle.kv.impl.monitor.views.PerfEvent;
import oracle.kv.impl.topo.ResourceId;
import oracle.kv.impl.util.registry.RegistryUtils;
import oracle.kv.stats.NodeMetrics;
import oracle.kv.stats.OperationMetrics;

import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;
import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.SimpleMetricName;
import org.metricssampler.resources.SamplerStats;

import com.sleepycat.utilint.Latency;

public class OracleNoSQLMetricsReader extends AbstractMetricsReader<OracleNoSQLInputConfig> implements BulkMetricsReader {
	private final Map<HostConfig, CommandServiceAPI> services = new HashMap<HostConfig, CommandServiceAPI>();

	public OracleNoSQLMetricsReader(final OracleNoSQLInputConfig config) {
		super(config);
	}

	@Override
	public void open() {
		for (final HostConfig host : config.getHosts()) {
			if(!services.containsKey(host)) {
				logger.info("Connecting to {}", host);
				try {
					SamplerStats.get().incConnectCount();
					final CommandServiceAPI service = RegistryUtils.getAdmin(host.getHost(), host.getPort());
					if (service != null) {
						services.put(host, service);
					}
				} catch (final RemoteException e) {
					logger.warn("Failed to fetch command service from " + host, e);
				} catch (final NotBoundException e) {
					logger.warn("Failed to fetch command service from " + host, e);
				}
			}
		}
	}

	@Override
	public void close() throws MetricReadException {
		// do not do anything - use persistent connection
	}

	@Override
	public void reset() {
		services.clear();
	}

	@Override
	public Iterable<MetricName> readNames() {
		return readAllMetrics().keySet();
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		for (final HostConfig host : config.getHosts()) {
			final CommandServiceAPI service = services.get(host);
			if (service != null) {
	    		try {
		    		logger.debug("Loading perfmon map from {}", host);
	    			final Map<ResourceId, PerfEvent> map = service.getPerfMap();
	        		convertEventsToMetrics(result, map.values());
		    		logger.debug("Perfmon map contains {} entries. Converting them to metrics", map.size());
	    		} catch (final RemoteException e) {
	    			logger.warn("Failed to fetch perfmon map from " + host, e);
					SamplerStats.get().incDisconnectCount();
	    			services.remove(host);
	    		}
			}
		}
		if (services.isEmpty()) {
			throw new MetricReadException("No hosts available");
		}
		return result;
	}

	protected void convertEventsToMetrics(final Map<MetricName, MetricValue> metrics, final Iterable<PerfEvent> events) {
		for (final PerfEvent event : events) {
			logger.debug("Processing event {}", event);
			addLatencyMetrics(event.getSingleInt(), event.getResourceId().getFullName() + ".single.interval", metrics);
			addLatencyMetrics(event.getSingleCum(), event.getResourceId().getFullName() + ".single.cumulative", metrics);
			addLatencyMetrics(event.getMultiInt(), event.getResourceId().getFullName() + ".multi.interval", metrics);
			addLatencyMetrics(event.getMultiCum(), event.getResourceId().getFullName() + ".multi.cumulative", metrics);
		}
	}

	protected void addLatencyMetrics(final LatencyInfo info, final String prefix, final Map<MetricName, MetricValue> result) {
		final long timestamp = info.getEnd();
		final Latency latency = info.getLatency();
		result.put(new SimpleMetricName(prefix + ".totalRequests", ""), new MetricValue(timestamp, latency.getTotalRequests()));
		result.put(new SimpleMetricName(prefix + ".totalOperations", ""), new MetricValue(timestamp, latency.getTotalOps()));
		result.put(new SimpleMetricName(prefix + ".overflowRequests", ""), new MetricValue(timestamp, latency.getRequestsOverflow()));
		result.put(new SimpleMetricName(prefix + ".min", ""), new MetricValue(timestamp, latency.getMin()));
		result.put(new SimpleMetricName(prefix + ".max", ""), new MetricValue(timestamp, latency.getMax()));
		result.put(new SimpleMetricName(prefix + ".avg", ""), new MetricValue(timestamp, Math.round(latency.getAvg())));
		result.put(new SimpleMetricName(prefix + ".percentile95", ""), new MetricValue(timestamp, latency.get95thPercent()));
		result.put(new SimpleMetricName(prefix + ".percentile99", ""), new MetricValue(timestamp, latency.get99thPercent()));
		result.put(new SimpleMetricName(prefix + ".tps", ""), new MetricValue(timestamp, info.getThroughputPerSec()));
	}

	protected void addNodeMetrics(final long timestamp, final List<NodeMetrics> nodeMetrics, final Map<MetricName, MetricValue> result) {
		if (nodeMetrics == null) {
			return;
		}
		for (final NodeMetrics item : nodeMetrics) {
			final String prefix = "nodes." + item.getDataCenterName() + "." + item.getNodeName() + ".";
			result.put(new SimpleMetricName(prefix + "avgLatency", "The trailing average latency (in ms) over all requests made to this node"), new MetricValue(timestamp, item.getAvLatencyMs()));
			result.put(new SimpleMetricName(prefix + "failedRequestCount", ""), new MetricValue(timestamp, item.getFailedRequestCount()));
			result.put(new SimpleMetricName(prefix + "totalRequestCount", "the total number of requests processed by the node"), new MetricValue(timestamp, item.getRequestCount()));
			result.put(new SimpleMetricName(prefix + "maxActiveRequestCount", "the number of requests that were concurrently active for this node at this KVS client"), new MetricValue(timestamp, item.getMaxActiveRequestCount()));
			result.put(new SimpleMetricName(prefix + "active", "1 if the node is currently active, that is, it's reachable and can service requests"), new MetricValue(timestamp, item.isActive() ? 1 : 0));
			result.put(new SimpleMetricName(prefix + "master", "1 if the node is currently a master"), new MetricValue(timestamp, item.isMaster() ? 1 : 0));
		}
	}

	protected void addOperationMetrics(final long timestamp, final List<OperationMetrics> opMetrics, final Map<MetricName, MetricValue> result) {
		if (opMetrics == null) {
			return;
		}
		for (final OperationMetrics item : opMetrics) {
			final String prefix = "operations." + item.getOperationName() + ".";
			result.put(new SimpleMetricName(prefix + "avgLatency", "the average latency associated with the operation in milli seconds"), new MetricValue(timestamp, Math.round(item.getAverageLatencyMs())));
			result.put(new SimpleMetricName(prefix + "maxLatency", "the maximum latency associated with the operation in milli seconds"), new MetricValue(timestamp, Math.round(item.getMaxLatencyMs())));
			result.put(new SimpleMetricName(prefix + "minLatency", "the minimum latency associated with the operation in milli seconds"), new MetricValue(timestamp, Math.round(item.getMinLatencyMs())));
			result.put(new SimpleMetricName(prefix + "totalCount", "the number of operations that were executed"), new MetricValue(timestamp, item.getTotalOps()));
		}
	}
}
