package org.metricssampler.extensions.oranosql;

import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.util.registry.RegistryUtils;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AnonymousOracleNoSQLMetricsReader extends AbstractOracleNoSQLMetricsReader {

	public AnonymousOracleNoSQLMetricsReader(final OracleNoSQLInputConfig config) {
		super(config);
	}

	@Override
	protected CommandServiceAPI loadCommandService() {
		for (final HostConfig host : config.getHosts()) {
            logger.info("Trying to get the command service API from {}", host);
            try {
                final CommandServiceAPI service = RegistryUtils.getAdmin(host.getHost(), host.getPort(), null);
                if (service.getMasterRmiAddress().getHost().equalsIgnoreCase(host.getHost()) && service.getMasterRmiAddress().getPort() == host.getPort()) {
                    return service;
                }
            } catch (final RemoteException e) {
                logger.warn("Failed to fetch command service from " + host, e);
            } catch (final NotBoundException e) {
                logger.warn("Failed to fetch command service from " + host, e);
            }
        }

        return null;
	}

}
