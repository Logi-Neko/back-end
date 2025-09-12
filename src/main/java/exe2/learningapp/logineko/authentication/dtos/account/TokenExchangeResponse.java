package exe2.learningapp.logineko.authentication.dtos.account;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TokenExchangeResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("refresh_expires_in") int refreshExpiresIn,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("id_token") String idToken,
        String scope
) {

}
