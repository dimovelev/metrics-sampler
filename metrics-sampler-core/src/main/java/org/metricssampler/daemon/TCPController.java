package org.metricssampler.daemon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;

import org.metricssampler.resources.SamplerTask;
import org.metricssampler.resources.SharedResource;
import org.metricssampler.service.Bootstrapper;

public interface TCPController extends Runnable {
	Bootstrapper getBootstrapper();
	Map<String, SamplerTask> getTasks();
	Map<String, SharedResource> getSharedResources();
	BufferedReader getClientReader();
	BufferedWriter getClientWriter();
}
