package org.metricssampler.extensions.oranosql;

import oracle.kv.*;
import oracle.kv.impl.admin.CommandServiceAPI;
import oracle.kv.impl.security.login.AdminLoginManager;
import oracle.kv.impl.security.util.KVStoreLogin;
import oracle.kv.impl.util.registry.RegistryUtils;
import org.apache.commons.io.IOUtils;
import org.metricssampler.extensions.oranosql.OracleNoSQLInputConfig.HostConfig;
import org.metricssampler.reader.OpenMetricsReaderException;

import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

public class AuthenticatedOracleNoSQLMetricsReader extends AbstractOracleNoSQLMetricsReader {
    private final String[] helperHosts;
    private final KVStoreLogin.CredentialsProvider credentialsProvider;
    private boolean storeConnectedOnce = false;

    public AuthenticatedOracleNoSQLMetricsReader(final OracleNoSQLInputConfig config) {
		super(config);
		helperHosts = convertHostsToArray(config.getHosts());
		credentialsProvider = new FixedCredentialsProvider(config.getLogin());
	}

    private String[] convertHostsToArray(List<HostConfig> hosts) {
	    final String[] result = new String[config.getHosts().size()];

        int i=0;
        for (HostConfig item : config.getHosts()) {
            result[i] = item.toString();
            i++;
        }

        return result;
    }

    protected void connectStore() {
        final LoginCredentials credentials = credentialsProvider.getCredentials();
        final Properties securityProps = createSecurityProperties(config.getTrustFile());
        final KVStoreConfig kvStoreConfig = new KVStoreConfig(config.getStoreName(), helperHosts);
        kvStoreConfig.setSecurityProperties(securityProps);
        logger.info("Connecting to the secured kv store [{}] at {} as [{}]", kvStoreConfig.getStoreName(), helperHosts, credentials.getUsername());
        try {
            final KVStore store = KVStoreFactory.getStore(kvStoreConfig, credentials, KVStoreLogin.makeReauthenticateHandler(credentialsProvider));
            IOUtils.closeQuietly(store);
        } catch (FaultException e) {
            throw new OpenMetricsReaderException("Failed to connect to store", e);
        }
    }

    protected Properties createSecurityProperties(Path trustStoreFile) {
        final Properties result = new Properties();
        result.setProperty(KVSecurityConstants.SSL_TRUSTSTORE_FILE_PROPERTY, trustStoreFile.toAbsolutePath().toString());
        result.setProperty(KVSecurityConstants.TRANSPORT_PROPERTY, KVSecurityConstants.SSL_TRANSPORT_NAME);
        return result;
    }

    @Override
	protected CommandServiceAPI loadCommandService() {
        connectStoreIfNecessary();
        return super.loadCommandService();
	}

    protected CommandServiceAPI lookupCommandService(HostConfig host) throws RemoteException, NotBoundException {
        final AdminLoginManager adminLoginManager = getAdminLoginManager(host);
        return RegistryUtils.getAdmin(host.getHost(), host.getPort(), adminLoginManager);
    }

    protected void connectStoreIfNecessary() {
        if (!storeConnectedOnce) {
            connectStore();
            storeConnectedOnce = true;
        }
    }

    protected AdminLoginManager getAdminLoginManager(HostConfig host) {
        if (credentialsProvider == null) {
            logger.info("No admin login manager required because no login credentials were configured");
            return null;
        } else {
            final LoginCredentials credentials = credentialsProvider.getCredentials();
            logger.info("Bootstrapping the admin login manager at [{}] as [{}]", host, credentials.getUsername());
            final AdminLoginManager result = new AdminLoginManager(credentials.getUsername(), true);
            try {
                final boolean bootstrapped = result.bootstrap(host.getHost(), host.getPort(), credentials);
                if (!bootstrapped) {
                    throw new OpenMetricsReaderException("Failed to bootstrap the admin login manager at [" + host.toString() + "] as [" + credentials.getUsername() + "]");
                }
            } catch (AuthenticationFailureException e) {
                throw new OpenMetricsReaderException("Failed to bootstrap the admin login manager due to authentication failure", e);
            }
            return result;
        }
    }

}
