package exe2.learningapp.logineko.authentication.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginKeycloakError {
    private String error;
    private String error_description;
}
