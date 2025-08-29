package exe2.learningapp.logineko.authentication.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
@Builder
public record TokenExchangeParams(@JsonProperty("grant_type") String grantType,
                                  @JsonProperty("client_id") String clientId,
                                  @JsonProperty("client_secret") String clientSecret,
                                  String scope) {

}
