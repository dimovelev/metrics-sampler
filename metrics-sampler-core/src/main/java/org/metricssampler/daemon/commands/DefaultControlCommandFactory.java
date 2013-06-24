package org.metricssampler.daemon.commands;

import org.metricssampler.daemon.TCPController;

public class DefaultControlCommandFactory implements ControlCommandFactory {
	private final TCPController controller;

	public DefaultControlCommandFactory(final TCPController controller) {
		this.controller = controller;
	}

	@Override
	public StatusCommand status() {
		return new StatusCommand(controller.getClientReader(), controller.getClientWriter());
	}

	@Override
	public ShutdownCommand shutdown() {
		return new ShutdownCommand(controller.getClientReader(), controller.getClientWriter(), controller.getBootstrapper());
	}

	@Override
	public DisableSamplerControlCommand disableSampler(final String name) {
		return new DisableSamplerControlCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name);
	}

	@Override
	public ResetSamplerControlCommand resetSampler(final String name) {
		return new ResetSamplerControlCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name);
	}

	@Override
	public EnableSamplerCommand enableSamplerForever(final String name) {
		return new EnableSamplerCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name, -1, -1L);
	}

	@Override
	public EnableSamplerCommand enableSamplerForTimes(final String name, final int times) {
		return new EnableSamplerCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name, times, -1L);
	}

	@Override
	public EnableSamplerCommand enableSamplerForDuration(final String name, final long seconds) {
		return new EnableSamplerCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name, -1, seconds);
	}

	@Override
	public InvalidSyntaxCommand invalidSyntax(final String line, final String msg) {
		return new InvalidSyntaxCommand(controller.getClientReader(), controller.getClientWriter(), line, msg);
	}

	@Override
	public ControlCommand startResource(final String name) {
		return new StartResourceCommand(controller.getClientReader(), controller.getClientWriter(), controller.getSharedResources(), name);
	}

	@Override
	public ControlCommand stopResource(final String name) {
		return new StopResourceCommand(controller.getClientReader(), controller.getClientWriter(), controller.getSharedResources(), name);
	}

	@Override
	public ControlCommand listSampler(final String name) {
		return new ListSamplerCommand(controller.getClientReader(), controller.getClientWriter(), controller.getTasks(), name);
	}


}
