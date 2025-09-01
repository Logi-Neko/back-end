package exe2.learningapp.logineko.authentication.client;

import exe2.learningapp.logineko.authentication.dtos.TokenExchangeParams;
import exe2.learningapp.logineko.authentication.dtos.TokenExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "identity-service", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TokenExchangeResponse exchangeToken(@RequestBody MultiValueMap<String, String> params);
}