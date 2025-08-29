package exe2.learningapp.logineko.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class Security {
    private final String[] PUBLIC_ENDPOINTS = {
            "/public/**", // Public endpoints that do not require authentication
            "/v3/api-docs/**", // OpenAPI documentation
            "/swagger-ui/**", // Swagger UI
            "/swagger-ui.html", // Swagger UI HTML
            "api/login/**"
    };

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf(csrf -> csrf.disable()) // disable CSRF để test API
//                .authorizeHttpRequests(request -> request.requestMatchers(PUBLIC_ENDPOINTS)
//                .permitAll()
//                .anyRequest()
//                .authenticated());
//
//        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
//                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
//        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//
//
//        return httpSecurity.build();
//    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) // disable CSRF để test API
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("api/login/**", "api/callback").permitAll() // cho phép public
                    .anyRequest().authenticated() // các API khác phải login
            );
    return http.build();
}


}
