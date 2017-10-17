package org.metricssampler.config;

public class HttpConnectionPoolConfig {
    private final int maxPerRoute;
    private final int maxTotal;
    private final int timeToLiveSeconds;

    public HttpConnectionPoolConfig(int maxPerRoute, int maxTotal, int timeToLiveSeconds) {
        this.maxPerRoute = maxPerRoute;
        this.maxTotal = maxTotal;
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    /**
     * @return the default maximal number of connections per route
     */
    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    /**
     * @return the default maximal number of connections overall
     */
    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * @return the total time to live (TTL) for persistent connections - the life span of persistent connections
     * regardless of their expiration setting. No persistent connection will be re-used past its TTL value.
     */
    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }
}
