package org.metricssampler.extensions.kafkamanager;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

@XStreamAlias("kafka-manager")
public class KafkaManagerInputXBean extends BaseHttpInputXBean {
    @XStreamAsAttribute
    @XStreamAlias("thread-pool")
    private String threadPool;

    public String getThreadPool() {
        return threadPool;
    }

    @Override
    protected InputConfig createConfig() {
        return new KafkaManagerInputConfig(
                getName(),
                getVariablesConfig(),
                parseUrl(),
                getUsername(), getPassword(),
                getHeadersAsMap(),
                isPreemptiveAuthEnabled(),
                createSocketOptionsConfig(),
                getThreadPool(),
                createConnectionPoolConfig()
        );
    }
}
