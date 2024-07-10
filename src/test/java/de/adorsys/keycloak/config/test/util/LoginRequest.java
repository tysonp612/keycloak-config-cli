package de.adorsys.keycloak.config.test.util;

public class LoginRequest {
    private final String url;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;

    public LoginRequest(String url, String realm, String clientId, String clientSecret, String username, String password) {
        this.url = url;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getRealm() {
        return realm;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
