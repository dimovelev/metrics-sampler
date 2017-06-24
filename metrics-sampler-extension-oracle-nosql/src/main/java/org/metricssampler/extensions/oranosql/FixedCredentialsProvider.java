package org.metricssampler.extensions.oranosql;

import oracle.kv.LoginCredentials;
import oracle.kv.PasswordCredentials;
import oracle.kv.impl.security.util.KVStoreLogin;
import org.metricssampler.config.LoginConfig;

public class FixedCredentialsProvider implements KVStoreLogin.CredentialsProvider {
    private final LoginCredentials credentials;

    public FixedCredentialsProvider(LoginConfig login) {
        this.credentials = new PasswordCredentials(login.getUsername(), login.getPassword().toCharArray());
    }

    @Override
    public LoginCredentials getCredentials() {
        return credentials;
    }
}
