package org.metricssampler.extensions.elasticsearch;

import org.metricssampler.config.BaseHttpInputConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.net.URL;
import java.util.Map;

public class ElasticSearchInputConfig extends BaseHttpInputConfig {
    private final boolean clusterStatsEnabled;
    private final boolean nodeStatsEnabled;

    public ElasticSearchInputConfig(String name, Map<String, Object> variables, URL url, String username, String password, Map<String, String> headers, boolean preemptiveAuthEnabled, SocketOptionsConfig socketOptions, boolean clusterStatsEnabled, boolean nodeStatsEnabled) {
        super(name, variables, url, username, password, headers, preemptiveAuthEnabled, socketOptions, null);
        this.clusterStatsEnabled = clusterStatsEnabled;
        this.nodeStatsEnabled = nodeStatsEnabled;
        final boolean anyStatsEnabled = clusterStatsEnabled || nodeStatsEnabled;
        if (!anyStatsEnabled) {
            throw new IllegalArgumentException("You must enable at least the cluster or the node stats");
        }
    }

    /**
     * @return true if the /_cluster/stats should be fetched
     */
    public boolean isClusterStatsEnabled() {
        return clusterStatsEnabled;
    }

    /**
     * @return true if the /_nodes/stats should be fetched
     */
    public boolean isNodeStatsEnabled() {
        return nodeStatsEnabled;
    }
}
