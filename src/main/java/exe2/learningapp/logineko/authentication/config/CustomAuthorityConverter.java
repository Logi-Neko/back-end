package exe2.learningapp.logineko.authentication.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String REALM_ACCESS = "realm_access";

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccessMap = source.getClaimAsMap(REALM_ACCESS);
        if (realmAccessMap == null) {
            return List.of();
        }

        Object roles = realmAccessMap.get("roles");
        if (roles instanceof List) {
            List<?> roleList = (List<?>) roles;
            return roleList.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> "ROLE_" + role) // hoặc tùy strategy
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}

