package org.metricssampler.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SamplerThreadPool;

public class GlobalRegistry {
	private static GlobalRegistry instance = new GlobalRegistry();

	public static GlobalRegistry getInstance() {
		return instance;
	}

	private final Set<SamplerTask> tasks = new CopyOnWriteArraySet<SamplerTask>();
	private final Set<SamplerThreadPool> samplerThreadPools = new HashSet<SamplerThreadPool>();

	public void addSamplerTask(final SamplerTask task) {
		tasks.add(task);
	}

	public Iterable<SamplerTask> getTasks() {
		return tasks;
	}

	public void addSamplerThreadPool(final SamplerThreadPool samplerThreadPool) {
		samplerThreadPools.add(samplerThreadPool);
	}

	public Iterable<SamplerThreadPool> getSamplerThreadPools() {
		return samplerThreadPools;
	}
}
