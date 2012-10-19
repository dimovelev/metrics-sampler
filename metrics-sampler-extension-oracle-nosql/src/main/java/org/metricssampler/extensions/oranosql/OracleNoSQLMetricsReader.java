package org.metricssampler.extensions.oranosql;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.kv.impl.admin.CommandService;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.measurement.LatencyInfo;
import oracle.kv.impl.monitor.views.PerfEvent;
import oracle.kv.impl.topo.ResourceId;
import oracle.kv.stats.NodeMetrics;
import oracle.kv.stats.OperationMetrics;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.reader.SimpleMetricName;

import com.sleepycat.utilint.Latency;

public class OracleNoSQLMetricsReader extends AbstractMetricsReader<OracleNoSQLInputConfig> implements BulkMetricsReader {
	private static final String COMMAND_SERVICE_NAME = "commandService";

	private CommandServiceAPI commonServiceAPI = null;

	public OracleNoSQLMetricsReader(final OracleNoSQLInputConfig config) {
		super(config);
	}

	@Override
	protected void defineCustomVariables(final Map<String, Object> variables) {
		variables.put("input.store", config.getStoreName());
	}

	@Override
	public void open() {
		if (commonServiceAPI != null) {
			return;
		}
		logger.info("Connecting to common service API");
		try {
			final Registry rmiRegistry = LocateRegistry.getRegistry(config.getHost(), config.getPort());
	        final Remote stub = rmiRegistry.lookup(COMMAND_SERVICE_NAME);
	        if (stub instanceof CommandService) {
	    		commonServiceAPI = CommandServiceAPI.wrap((CommandService) stub);
	        } else {
	        	throw new OpenMetricsReaderException("Remote stub named " + COMMAND_SERVICE_NAME + " is not instance of " + CommandService.class);
	        }
		} catch (final RemoteException e) {
			throw new OpenMetricsReaderException("Could not get RMI registry at " + config.getHost() + ":" + config.getPort(), e);
		} catch (final NotBoundException e) {
			throw new OpenMetricsReaderException("Could not find stub " + COMMAND_SERVICE_NAME + ": " + e.getMessage(), e);
		}
	}

	@Override
	public void close() throws MetricReadException {
		// do not do anything - use persistent connection
	}

	@Override
	public Iterable<MetricName> readNames() {
		return readAllMetrics().keySet();
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		assert commonServiceAPI != null;
		try {
    		final Map<ResourceId, PerfEvent> map = commonServiceAPI.getPerfMap();
    		return convertEventsToMetrics(map.values());
		} catch (final RemoteException e) {
			commonServiceAPI = null;
			throw new MetricReadException("Failed to get perf map", e);
		}
	}

	protected Map<MetricName, MetricValue> convertEventsToMetrics(final Iterable<PerfEvent> events) {
		final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
		for (final PerfEvent event : events) {

			addLatencyMetrics(event.getSingleInt(), event.getResourceId().getFullName() + ".single.interval", result);
			addLatencyMetrics(event.getSingleCum(), event.getResourceId().getFullName() + ".single.cumulative", result);
			addLatencyMetrics(event.getMultiInt(), event.getResourceId().getFullName() + ".multi.interval", result);
			addLatencyMetrics(event.getMultiCum(), event.getResourceId().getFullName() + ".multi.cumulative", result);
		}
		return result;
	}

	protected void addLatencyMetrics(final LatencyInfo info, final String prefix, final Map<MetricName, MetricValue> result) {
		final long timestamp = info.getEnd();
		final Latency latency = info.getLatency();
		result.put(new SimpleMetricName(prefix + ".totalOperations", ""), new MetricValue(timestamp, latency.getTotalOps()));
		result.put(new SimpleMetricName(prefix + ".operationsOverflow", ""), new MetricValue(timestamp, latency.getOpsOverflow()));
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
