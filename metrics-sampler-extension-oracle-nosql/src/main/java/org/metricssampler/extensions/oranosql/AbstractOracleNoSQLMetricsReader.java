package org.metricssampler.extensions.oranosql;

import com.sleepycat.utilint.Latency;
import oracle.kv.KVSecurityException;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.measurement.LatencyInfo;
import oracle.kv.impl.monitor.views.PerfEvent;
import oracle.kv.impl.monitor.views.ServiceChange;
import oracle.kv.impl.topo.RepNodeId;
import oracle.kv.impl.topo.StorageNodeId;
import oracle.kv.impl.topo.Topology;
import org.metricssampler.reader.*;
import org.metricssampler.resources.SamplerStats;

import java.net.URI;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractOracleNoSQLMetricsReader extends AbstractMetricsReader<OracleNoSQLInputConfig> implements BulkMetricsReader {
    protected CommandServiceAPI service = null;

    public AbstractOracleNoSQLMetricsReader(OracleNoSQLInputConfig config) {
        super(config);
    }


    @Override
    public void open() {
        if (service == null) {
            SamplerStats.get().incConnectCount();
            service = loadCommandService();
        }
        if (service == null) {
            throw new OpenMetricsReaderException("Failed to open command service api to the admin");
        }
    }

    protected CommandServiceAPI loadCommandService() {
        for (final OracleNoSQLInputConfig.HostConfig host : config.getHosts()) {
            logger.info("Trying to get the command service API from {}", host);
            try {
                final CommandServiceAPI commandService = loadCommandService(host);
                if (commandService != null) {
                    return commandService;
                }
            } catch (final RemoteException e) {
                logger.warn("Failed to fetch command service from " + host, e);
            } catch (final NotBoundException e) {
                logger.warn("Failed to fetch command service from " + host, e);
            }
        }

        return null;
    }

    @Override
    public void close() throws MetricReadException {
        // do not do anything - use persistent connection
    }

    @Override
    public void reset() {
        SamplerStats.get().incDisconnectCount();
        service = null;
    }

    @Override
    public Iterable<MetricName> readNames() {
        return readAllMetrics().keySet();
    }

    @Override
    public Map<MetricName, MetricValue> readAllMetrics() throws MetricReadException {
        if (service == null) {
            throw new MetricReadException("No command service available");
        }
        final Map<MetricName, MetricValue> result = new HashMap<>();

        boolean failureEncountered = false;
        try {
            addStatusMetrics(result);
        } catch (RemoteException | KVSecurityException e) {
            logger.warn("Failed to fetch the status metrics", e);
            failureEncountered = true;
        }

        try {
            addPerfMapMetrics(result);
        } catch (RemoteException | KVSecurityException e) {
            logger.warn("Failed to fetch the perf map metrics", e);
            failureEncountered = true;
        }

        try {
            addTopologyMetrics(result);
        } catch (RemoteException e) {
            logger.warn("Failed to fetch the topology metrics", e);
            failureEncountered = true;
        }

        if (failureEncountered) {
            reset();
        }

        return result;
    }

    protected void addTopologyMetrics(Map<MetricName, MetricValue> result) throws RemoteException {
        final long timestamp = System.currentTimeMillis();
        final Topology topology = service.getTopology();
        for (StorageNodeId snId : topology.getStorageNodeIds()) {
            final Set<RepNodeId> hostedRepNodes = topology.getHostedRepNodeIds(snId);
            for (RepNodeId rnId : topology.getRepNodeIds()) {
                final boolean hosted = hostedRepNodes.contains(rnId);
                result.put(new SimpleMetricName(snId.getFullName() + ".hosting." + rnId.getFullName(), ""), new MetricValue(timestamp, hosted ? 1 : 0));
            }
        }
    }

    private void addStatusMetrics(Map<MetricName, MetricValue> result) throws RemoteException {
        logger.debug("Loading the status map");
        for (ServiceChange item : service.getStatusMap().values()) {
            final long timestamp = System.currentTimeMillis();
            final String name = item.getTarget().getFullName();
            result.put(new SimpleMetricName(name + ".status.ordinal", "0-STARTING,1-WAITING_FOR_DEPLOY,2-RUNNING,3-STOPPING,4-STOPPED,5-ERROR_RESTARTING,6-ERROR_NO_RESTART,7-UNREACHABLE,8-EXPECTED_RESTARTING"), new MetricValue(timestamp, item.getStatus().ordinal()));
            result.put(new SimpleMetricName(name + ".status.age", "The number of milliseconds since this change"), new MetricValue(timestamp, timestamp - item.getChangeTime()));
        }
    }

    protected void addPerfMapMetrics(final Map<MetricName, MetricValue> metrics) throws RemoteException {
        logger.debug("Loading the perf map");
        for (final PerfEvent event : service.getPerfMap().values()) {
            logger.debug("Processing event {}", event);
            addLatencyMetrics(event.getSingleInt(), event.getResourceId().getFullName() + ".ops.single.interval", metrics);
            addLatencyMetrics(event.getSingleCum(), event.getResourceId().getFullName() + ".ops.single.cumulative", metrics);
            addLatencyMetrics(event.getMultiInt(), event.getResourceId().getFullName() + ".ops.multi.interval", metrics);
            addLatencyMetrics(event.getMultiCum(), event.getResourceId().getFullName() + ".ops.multi.cumulative", metrics);
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

    protected CommandServiceAPI loadCommandService(OracleNoSQLInputConfig.HostConfig host) throws RemoteException, NotBoundException {
        final CommandServiceAPI service = lookupCommandService(host);
        final URI masterUri = service.getMasterRmiAddress();
        if (masterUri.getHost().equalsIgnoreCase(host.getHost()) && masterUri.getPort() == host.getPort()) {
            logger.info("Found command service API on {} at {}", host,  masterUri);
            return service;
        } else {
            logger.info("Connected to {} but it is not the master at {} so disconnecting from it. Trying the master directly.", host, masterUri);
            return loadCommandService(new OracleNoSQLInputConfig.HostConfig(masterUri.getHost(), masterUri.getPort()));
        }
    }

    protected abstract CommandServiceAPI lookupCommandService(OracleNoSQLInputConfig.HostConfig host) throws RemoteException, NotBoundException;
}
