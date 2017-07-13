package org.metricssampler.extensions.oranosql;

import com.sleepycat.utilint.Latency;
import oracle.kv.KVSecurityException;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.measurement.LatencyInfo;
import oracle.kv.impl.monitor.views.PerfEvent;
import oracle.kv.impl.monitor.views.ServiceChange;
import oracle.kv.impl.topo.RepNodeId;
import oracle.kv.impl.topo.ResourceId.ResourceType;
import oracle.kv.impl.topo.StorageNodeId;
import oracle.kv.impl.topo.Topology;
import org.metricssampler.reader.*;
import org.metricssampler.resources.SamplerStats;

import java.net.URI;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
    public Metrics readAllMetrics() throws MetricReadException {
        if (service == null) {
            throw new MetricReadException("No command service available");
        }
        final Metrics result = new Metrics();

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

    protected void addTopologyMetrics(Metrics result) throws RemoteException {
        final long timestamp = System.currentTimeMillis();
        final Topology topology = service.getTopology();
        for (StorageNodeId snId : topology.getStorageNodeIds()) {
            final Set<RepNodeId> hostedRepNodes = topology.getHostedRepNodeIds(snId);
            for (RepNodeId rnId : topology.getRepNodeIds()) {
                final boolean hosted = hostedRepNodes.contains(rnId);
                final String name = snId.getType().name().toLowerCase() + ".nodes." + snId.getFullName() + ".hosting." + rnId.getFullName();
                result.add(name, "Whether replication node " + rnId.getFullName() + " is hosted on " + snId.getFullName(), timestamp, hosted ? 1 : 0);
            }
        }
    }

    private void addStatusMetrics(Metrics result) throws RemoteException {
        logger.debug("Loading the status map");
        final ResourceTypeServiceStatusMetrics statusMetrics = new ResourceTypeServiceStatusMetrics();

        for (ServiceChange item : service.getStatusMap().values()) {
            final ResourceType type = item.getTarget().getType();
            statusMetrics.add(type, item.getStatus());
            final String name = type.name().toLowerCase() + ".nodes." + item.getTarget().getFullName();
            final long timestamp = System.currentTimeMillis();
            result.add(name + ".status.ordinal", "0-STARTING,1-WAITING_FOR_DEPLOY,2-RUNNING,3-STOPPING,4-STOPPED,5-ERROR_RESTARTING,6-ERROR_NO_RESTART,7-UNREACHABLE,8-EXPECTED_RESTARTING", timestamp, item.getStatus().ordinal());
            result.add(name + ".status.age", "The number of milliseconds since this change", timestamp, timestamp - item.getChangeTime());
        }

        statusMetrics.addMetrics(result);
    }

    protected void addPerfMapMetrics(final Metrics metrics) throws RemoteException {
        logger.debug("Loading the perf map");
        for (final PerfEvent event : service.getPerfMap().values()) {
            logger.debug("Processing event {}", event);
            final String prefix = event.getResourceId().getType().name().toLowerCase() + ".nodes." + event.getResourceId().getFullName();
            addLatencyMetrics(event.getSingleInt(), prefix + ".ops.single.interval", metrics);
            addLatencyMetrics(event.getSingleCum(), prefix + ".ops.single.cumulative", metrics);
            addLatencyMetrics(event.getMultiInt(), prefix + ".ops.multi.interval", metrics);
            addLatencyMetrics(event.getMultiCum(), prefix + ".ops.multi.cumulative", metrics);
        }
    }

    protected void addLatencyMetrics(final LatencyInfo info, final String prefix, final Metrics result) {
        final long timestamp = info.getEnd();
        final Latency latency = info.getLatency();
        result.add(prefix + ".totalRequests", timestamp, latency.getTotalRequests());
        result.add(prefix + ".totalOperations", timestamp, latency.getTotalOps());
        result.add(prefix + ".overflowRequests", timestamp, latency.getRequestsOverflow());
        result.add(prefix + ".min", timestamp, latency.getMin());
        result.add(prefix + ".max", timestamp, latency.getMax());
        result.add(prefix + ".avg", timestamp, Math.round(latency.getAvg()));
        result.add(prefix + ".percentile95", timestamp, latency.get95thPercent());
        result.add(prefix + ".percentile99", timestamp, latency.get99thPercent());
        result.add(prefix + ".tps", timestamp, info.getThroughputPerSec());
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
