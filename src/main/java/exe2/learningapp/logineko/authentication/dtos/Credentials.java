package exe2.learningapp.logineko.authentication.dtos;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;


public record Credentials(
        String type,
        String value,
        Boolean temporary
) {
}
