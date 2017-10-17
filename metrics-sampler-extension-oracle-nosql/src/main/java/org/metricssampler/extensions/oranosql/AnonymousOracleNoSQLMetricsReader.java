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
    protected CommandServiceAPI lookupCommandService(HostConfig host) throws RemoteException, NotBoundException {
        return RegistryUtils.getAdmin(host.getHost(), host.getPort(), null);
    }

}
