package exe2.learningapp.logineko.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        org.springframework.web.cors.CorsConfiguration corsConfiguration =
                new org.springframework.web.cors.CorsConfiguration();

        // Allow React.js frontend running on port 3000
        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:3000");
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:5173");

        // Allow device connections from 192.168.101.3 (both HTTP and HTTPS)
        corsConfiguration.addAllowedOrigin("http://192.168.101.3:3000");
        corsConfiguration.addAllowedOrigin("http://192.168.101.3:8081");
        corsConfiguration.addAllowedOrigin("https://192.168.101.3:3000");
        corsConfiguration.addAllowedOrigin("https://192.168.101.3:8081");
        corsConfiguration.addAllowedOrigin("https://auth.logineko.edu.vn");
        corsConfiguration.addAllowedOrigin("https://api.logineko.edu.vn");
        // Allow any origin from the local network (for Android and other devices)
        corsConfiguration.addAllowedOriginPattern("http://192.168.*.*:*");
        corsConfiguration.addAllowedOriginPattern("https://192.168.*.*:*");
        // For Android development/testing, you might need to allow localhost on different ports
        corsConfiguration.addAllowedOriginPattern("http://localhost:*");
        corsConfiguration.addAllowedOriginPattern("http://127.0.0.1:*");
        corsConfiguration.addAllowedOriginPattern("http://10.0.2.2:*"); // Android emulator localhost

        // Allow common HTTP methods
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("PATCH");
        corsConfiguration.addAllowedMethod("OPTIONS");

        // Allow common headers
        corsConfiguration.addAllowedHeader("*");

        // Allow credentials (cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
