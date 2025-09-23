package exe2.learningapp.logineko.authentication.service;

import org.keycloak.admin.client.Keycloak;

public interface KeycloakService {
    Keycloak getKeyCloakInstance();
}
