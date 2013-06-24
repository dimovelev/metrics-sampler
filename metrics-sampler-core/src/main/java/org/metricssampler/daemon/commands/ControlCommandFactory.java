package org.metricssampler.daemon.commands;

public interface ControlCommandFactory {
	ControlCommand status();
	ControlCommand shutdown();
	ControlCommand disableSampler(String name);
	ControlCommand enableSamplerForever(String name);
	ControlCommand enableSamplerForTimes(String name, int times);
	ControlCommand enableSamplerForDuration(String name, long seconds);
	ControlCommand resetSampler(String name);
	ControlCommand invalidSyntax(String line, String msg);
	ControlCommand startResource(String name);
	ControlCommand stopResource(String name);
	ControlCommand listSampler(String name);
}
