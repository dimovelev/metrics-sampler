package org.metricssampler.extensions.elasticsearch;

import org.metricssampler.config.InputConfig;
import org.metricssampler.reader.MetricsReader;
import org.metricssampler.service.AbstractExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ElasticSearchExtension extends AbstractExtension {
    @Override
    public Collection<Class<?>> getXBeans() {
        final List<Class<?>> result = new LinkedList<Class<?>>();
        result.add(ElasticSearchInputXBean.class);
        return result;
    }

    @Override
    public boolean supportsInput(final InputConfig config) {
        return config instanceof ElasticSearchInputConfig;
    }

    @Override
    protected MetricsReader doNewReader(final InputConfig config) {
        if (config instanceof ElasticSearchInputConfig) {
            return new ElasticSearchMetricsReader((ElasticSearchInputConfig) config);
        } else {
            throw new IllegalArgumentException("Unsupported reader config: " + config);
        }
    }
}
