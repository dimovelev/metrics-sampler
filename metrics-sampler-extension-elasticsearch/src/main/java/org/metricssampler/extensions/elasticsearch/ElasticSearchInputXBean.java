package org.metricssampler.extensions.elasticsearch;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

@XStreamAlias("elastic-search")
public class ElasticSearchInputXBean extends BaseHttpInputXBean {
    @XStreamAsAttribute
    @XStreamAlias("cluster-stats-enabled")
    private Boolean clusterStatsEnabled;

    @XStreamAsAttribute
    @XStreamAlias("node-stats-enabled")
    private Boolean nodeStatsEnabled;

    @Override
    protected InputConfig createConfig() {
        return new ElasticSearchInputConfig(
                getName(),
                getVariablesConfig(),
                parseUrl(),
                getUsername(), getPassword(),
                getHeadersAsMap(),
                isPreemptiveAuthEnabled(),
                createSocketOptionsConfig(),
                clusterStatsEnabled != null ? clusterStatsEnabled : true,
                nodeStatsEnabled != null ? nodeStatsEnabled : true
        );
    }

    public Boolean getClusterStatsEnabled() {
        return clusterStatsEnabled;
    }

    public void setClusterStatsEnabled(Boolean clusterStatsEnabled) {
        this.clusterStatsEnabled = clusterStatsEnabled;
    }

    public Boolean getNodeStatsEnabled() {
        return nodeStatsEnabled;
    }

    public void setNodeStatsEnabled(Boolean nodeStatsEnabled) {
        this.nodeStatsEnabled = nodeStatsEnabled;
    }
}
