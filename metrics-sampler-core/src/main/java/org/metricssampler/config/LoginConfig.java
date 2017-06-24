package org.metricssampler.config;

public class LoginConfig {
    private final String username;
    private final String password;

    public LoginConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
