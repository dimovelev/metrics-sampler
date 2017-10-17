package org.metricssampler.service;

import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SharedResource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GlobalRegistry {
	private static GlobalRegistry instance = new GlobalRegistry();

	public static GlobalRegistry getInstance() {
		return instance;
	}

	private final Set<SamplerTask> tasks = new CopyOnWriteArraySet<>();
	private final Set<SharedResource> sharedResources = new HashSet<>();

	public void addSamplerTask(final SamplerTask task) {
		tasks.add(task);
	}

	public Iterable<SamplerTask> getTasks() {
		return tasks;
	}

	public Iterable<SharedResource> getSharedResources() {
		return sharedResources;
	}

	public void addSharedResource(final SharedResource sharedResource) {
		sharedResources.add(sharedResource);
	}
}
