package exe2.learningapp.logineko.authentication.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


import java.util.List;
@Builder
public record UserCreationParams(
        String username,
        String email,
        @JsonProperty("emailVerified") Boolean emailVerified,
        Boolean enabled,
        String firstName,
        String lastName,
        List<Credentials> credentials
) {
}
