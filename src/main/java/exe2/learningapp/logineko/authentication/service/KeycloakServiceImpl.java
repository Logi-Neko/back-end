//package exe2.learningapp.logineko.authentication.service;
//
//import exe2.learningapp.logineko.authentication.dtos.UserInfo;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class KeycloakServiceImpl implements KeycloakService {
//    private final RestTemplate restTemplate;
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
//    @Value("${keycloak.auth-server-url}")
//    private String keycloakUrl;
//
//    @Value("${keycloak.resource}")
//    private String clientId;
//
//    @Value("${keycloak.credentials.secret}")
//    private String clientSecret;
//
//    @Value("${app.redirect-uri}")
//    private String redirectUri;
//
//    @Override
//    public  exchangeCodeForTokens(String code) {
//        String tokenEndpoint = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("code", code);
//        body.add("redirect_uri", redirectUri);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
//                tokenEndpoint, request, TokenResponse.class);
//
//        return response.getBody();
//    }
//
//    @Override
//    public UserInfo getUserInfo(String token) {
//        String userInfoEndpoint = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//
//        HttpEntity<String> request = new HttpEntity<>(headers);
//
//        ResponseEntity<UserInfo> response = restTemplate.exchange(
//                userInfoEndpoint, HttpMethod.GET, request, UserInfo.class);
//
//        return response.getBody();
//    }
//}
