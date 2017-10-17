package org.metricssampler.extensions.kafkamanager;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.resources.SamplerThreadPool;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.AbstractExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KafkaManagerExtension extends AbstractExtension {
    @Override
    public Collection<Class<?>> getXBeans() {
        final List<Class<?>> result = new ArrayList<>();
        result.add(KafkaManagerInputXBean.class);
        return result;
    }

    @Override
    public boolean supportsInput(final InputConfig config) {
        return config instanceof KafkaManagerInputConfig;
    }

    @Override
    protected MetricsReader doNewReader(final InputConfig config) {
        if (config instanceof KafkaManagerInputConfig) {
            final KafkaManagerInputConfig typedConfig = (KafkaManagerInputConfig) config;
            final SharedResource sharedResource = getGlobalFactory().getSharedResource(typedConfig.getThreadPool());
            if (sharedResource instanceof SamplerThreadPool) {
                return new KafkaManagerMetricsReader(typedConfig, (SamplerThreadPool) sharedResource);
            } else {
                throw new ConfigurationException(typedConfig.getThreadPool() + " is not a thread pool pool: " + sharedResource);
            }
        } else {
            throw new IllegalArgumentException("Unsupported reader config: " + config);
        }
    }
}
