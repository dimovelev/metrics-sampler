package org.metricssampler.resources;

import java.util.concurrent.ScheduledExecutorService;

public interface SamplerThreadPool extends SharedResource  {
	abstract ScheduledExecutorService getExecutorService();
}