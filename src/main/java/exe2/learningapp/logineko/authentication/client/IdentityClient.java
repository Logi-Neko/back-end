package exe2.learningapp.logineko.authentication.client;

import exe2.learningapp.logineko.authentication.dtos.TokenExchangeParams;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "identity-service", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Object exchangeToken(@QueryMap TokenExchangeParams param);
}
