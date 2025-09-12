package exe2.learningapp.logineko.authentication.dtos.account;

import lombok.Builder;

@Builder
public record Credentials(
        String type,
        String value,
        Boolean temporary
) {
}
