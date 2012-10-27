package org.metricssampler.daemon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;

import org.metricssampler.service.Bootstrapper;

public interface TCPController extends Runnable {
	Bootstrapper getBootstrapper();
	Map<String, SamplerTask> getTasks();
	BufferedReader getClientReader();
	BufferedWriter getClientWriter();
}
