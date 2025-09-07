package exe2.learningapp.logineko.authentication.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

    @JsonProperty("sub")
    String subject; // Unique user ID from provider

    @JsonProperty("email")
    String email;

    @JsonProperty("email_verified")
    Boolean emailVerified;

    @JsonProperty("name")
    String name;

    @JsonProperty("given_name")
    String firstName;

    @JsonProperty("family_name")
    String lastName;

    @JsonProperty("picture")
    String profilePicture;

    @JsonProperty("locale")
    String locale;

    @JsonProperty("preferred_username")
    String preferredUsername;

    // Additional Keycloak specific fields
    @JsonProperty("realm_access")
    RealmAccess realmAccess;

    @JsonProperty("resource_access")
    Object resourceAccess;

    // Custom fields for your application
    String provider; // "google", "keycloak", etc.
    LocalDateTime lastLogin;
    Boolean active;

    // Helper methods
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        if (firstName != null || lastName != null) {
            return String.format("%s %s",
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "").trim();
        }
        if (preferredUsername != null) {
            return preferredUsername;
        }
        return email;
    }

    public String getFullName() {
        return String.format("%s %s",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "").trim();
    }

    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }

    public boolean isActive() {
        return active != null && active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RealmAccess {
        @JsonProperty("roles")
        String[] roles;
    }
}