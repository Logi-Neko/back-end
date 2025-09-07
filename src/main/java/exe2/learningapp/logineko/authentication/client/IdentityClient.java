package exe2.learningapp.logineko.authentication.client;

import exe2.learningapp.logineko.authentication.dtos.TokenExchangeParams;
import exe2.learningapp.logineko.authentication.dtos.TokenExchangeResponse;
import exe2.learningapp.logineko.authentication.dtos.UserCreationParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "identity-service", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TokenExchangeResponse exchangeToken(@RequestBody MultiValueMap<String, String> params);

    @PostMapping(value = "/admin/realms/${keycloak.realm}/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token, @RequestBody UserCreationParams userCreationParams);

}