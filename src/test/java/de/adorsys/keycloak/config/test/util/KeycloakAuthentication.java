package de.adorsys.keycloak.config.test.util;

import de.adorsys.keycloak.config.properties.KeycloakConfigProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAuthentication {
    private final KeycloakConfigProperties keycloakConfigProperties;

    @Autowired
    public KeycloakAuthentication(KeycloakConfigProperties keycloakConfigProperties) {
        this.keycloakConfigProperties = keycloakConfigProperties;
    }

    public AccessTokenResponse login(String realm, String clientId, String clientSecret, String username, String password) {
        LoginRequest loginRequest = new LoginRequest(
                keycloakConfigProperties.getUrl(),
                realm,
                clientId,
                clientSecret,
                username,
                password
        );
        return login(loginRequest);
    }

    public AccessTokenResponse login(LoginRequest loginRequest) {
        return Keycloak.getInstance(
                loginRequest.getUrl(),
                loginRequest.getRealm(),
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                loginRequest.getClientId(),
                loginRequest.getClientSecret()
        ).tokenManager().getAccessToken();
    }
}
