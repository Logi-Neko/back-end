package exe2.learningapp.logineko.authentication.dtos.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record TokenExchangeParams(@JsonProperty("grant_type") String grantType,
                                  @JsonProperty("client_id") String clientId,
                                  @JsonProperty("client_secret") String clientSecret,
                                  String scope) {

}
