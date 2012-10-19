package org.metricssampler.extensions.oranosql;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import oracle.kv.impl.admin.CommandService;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.monitor.views.PerfEvent;
import oracle.kv.impl.topo.ResourceId;

import org.metricssampler.reader.OpenMetricsReaderException;

public class OracleNoSQLAdminConnection {
	private static final String COMMAND_SERVICE_NAME = "commandService";

	private final OracleNoSQLInputConfig config;

	private CommandServiceAPI commonServiceAPI;

	public OracleNoSQLAdminConnection(final OracleNoSQLInputConfig config) {
		this.config = config;
	}

	public void connect() {
		try {
			final Registry rmiRegistry = LocateRegistry.getRegistry(config.getHost(), config.getPort());
	        final Remote stub = rmiRegistry.lookup(COMMAND_SERVICE_NAME);
	        if (stub instanceof CommandService) {
	    		commonServiceAPI = CommandServiceAPI.wrap((CommandService) stub);
	        } else {
	        	throw new OpenMetricsReaderException("Remote stub named " + COMMAND_SERVICE_NAME + " is not instance of " + CommandService.class);
	        }
		} catch (final RemoteException e) {
			throw new OpenMetricsReaderException("Could not get RMI registry at " + config.getHost() + ":" + config.getPort(), e);
		} catch (final NotBoundException e) {
			throw new OpenMetricsReaderException("Could not find stub " + COMMAND_SERVICE_NAME + ": " + e.getMessage(), e);
		}
	}

	public void disconnect() {
		commonServiceAPI = null;
	}

	public Map<ResourceId, PerfEvent> getEvents() throws RemoteException {
		return commonServiceAPI.getPerfMap();
	}
}
