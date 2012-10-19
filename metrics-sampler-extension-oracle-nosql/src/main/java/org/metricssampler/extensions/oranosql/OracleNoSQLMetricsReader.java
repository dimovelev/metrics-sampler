package org.metricssampler.extensions.oranosql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.stats.KVStats;
import oracle.kv.stats.NodeMetrics;
import oracle.kv.stats.OperationMetrics;

import org.metricssampler.reader.AbstractMetricsReader;
import org.metricssampler.reader.BulkMetricsReader;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricReadException;
import org.metricssampler.reader.MetricValue;
import org.metricssampler.reader.OpenMetricsReaderException;
import org.metricssampler.reader.SimpleMetricName;

public class OracleNoSQLMetricsReader extends AbstractMetricsReader<OracleNoSQLInputConfig> implements BulkMetricsReader {
	private final KVStoreConfig storeConfig;
	private KVStore kvStore;
	
	public OracleNoSQLMetricsReader(final OracleNoSQLInputConfig config) {
		super(config);
		storeConfig = new KVStoreConfig(config.getStoreName(), config.getHosts()); 
	}

	@Override
	protected void defineCustomVariables(final Map<String, Object> variables) {
		variables.put("storeName", config.getStoreName());
	}

	@Override
	public void open() {
		closeStore();
		assert kvStore == null;
		logger.info("Connecting KVStore");
		try {
			kvStore = KVStoreFactory.getStore(storeConfig);
		} catch (final FaultException e) {
			throw new OpenMetricsReaderException("Failed to get store", e);
		}
	}

	private void closeStore() {
		if (kvStore != null) {
			kvStore.close();
			kvStore = null;
		}
	}
	
	@Override
	public void close() throws MetricReadException {
		closeStore();
	}

	@Override
	public Iterable<MetricName> readNames() {
		return readAllMetrics().keySet();
	}

	@Override
	public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
		assert kvStore != null;
		final long timestamp = System.currentTimeMillis();
		try {
			final KVStats stats = kvStore.getStats(false);
			final Map<MetricName, MetricValue> result = new HashMap<MetricName, MetricValue>();
			addNodeMetrics(timestamp, stats.getNodeMetrics(), result);
			addOperationMetrics(timestamp, stats.getOpMetrics(), result);
			return result;
		} catch (final FaultException e) {
			throw new MetricReadException("Failed to get stats", e);
		}
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
