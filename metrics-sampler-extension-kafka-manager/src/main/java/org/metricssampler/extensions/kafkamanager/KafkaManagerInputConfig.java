package org.metricssampler.extensions.kafkamanager;

import org.metricssampler.config.BaseHttpInputConfig;
import org.metricssampler.config.HttpConnectionPoolConfig;
import org.metricssampler.config.SocketOptionsConfig;
import org.metricssampler.util.Preconditions;

import java.net.URL;
import java.util.Map;

public class KafkaManagerInputConfig extends BaseHttpInputConfig {
    private final String threadPool;

    public KafkaManagerInputConfig(String name, Map<String, Object> variables, URL url, String username, String password, Map<String, String> headers, boolean preemptiveAuthEnabled, SocketOptionsConfig socketOptions, String threadPool, HttpConnectionPoolConfig connectionPool) {
        super(name, variables, url, username, password, headers, preemptiveAuthEnabled, socketOptions, connectionPool);
        this.threadPool = threadPool;
        Preconditions.checkArgumentNotNullNorEmpty(threadPool, "The thread pool is required");
    }

    /**
     * @return the name of the thread-pool to use when concurrently fetching data via HTTP
     */
    public String getThreadPool() {
        return threadPool;
    }
}
