package exe2.learningapp.logineko.authentication.dtos;

import lombok.Builder;

@Builder
public record Credentials(
        String type,
        String value,
        Boolean temporary
) {
}
