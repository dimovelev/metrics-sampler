package org.metricssampler.resources;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.metricssampler.sampler.Sampler;

public interface SamplerThreadPool extends SharedResource {
    /**
     * Schedule the sampler in the thread pool
     *
     * @param sampler the sampler to be scheduled
     * @return the actual runnable task that can be used to control the sampler
     */
    SamplerTask schedule(Sampler sampler);

    String getName();

    <T> Future<T> submit(Callable<T> task);
}