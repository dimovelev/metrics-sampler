package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.HttpConnectionPoolConfig;

@XStreamAlias("http-connection-pool")
public class HttpConnectionPoolXBean extends XBean {
    @XStreamAlias("max-per-route")
    @XStreamAsAttribute
    private Integer maxPerRoute = 20;

    @XStreamAlias("max-total")
    @XStreamAsAttribute
    private Integer maxTotal = 100;

    @XStreamAlias("ttl-seconds")
    @XStreamAsAttribute
    private Integer timeToLive = 120;

    public Integer getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(Integer maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    public HttpConnectionPoolConfig toConfig() {
        return new HttpConnectionPoolConfig(
                getMaxPerRoute() != null ? getMaxPerRoute() : 0,
                getMaxTotal() != null ? getMaxTotal() : 0,
                getTimeToLive() != null ? getTimeToLive() : 0
        );
    }
}
