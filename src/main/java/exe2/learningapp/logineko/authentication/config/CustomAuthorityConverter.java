package exe2.learningapp.logineko.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final String RealmAccess = "realm_access";

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccessMap = source.getClaimAsMap(RealmAccess);
        Object roles = realmAccessMap.get("roles");
        if(roles instanceof List<?> roleList) {
            return roleList.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> "ROLE_" + role)
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
