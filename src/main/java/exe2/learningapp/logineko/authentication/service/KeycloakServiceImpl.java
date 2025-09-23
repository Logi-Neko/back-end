package exe2.learningapp.logineko.authentication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Transactional
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${app.redirect-uri}")
    private String redirectUri;

    @Value("${keycloak.auth-server}")
    private String keycloakUrlWeb;


    @Override
    public Keycloak getKeyCloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakUrlWeb)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
