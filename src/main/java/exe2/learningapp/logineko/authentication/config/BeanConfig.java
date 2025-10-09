package exe2.learningapp.logineko.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(new JavaTimeModule());
        builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.featuresToDisable(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
        return builder;
    }
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "oAuth2";

        // Cấu hình các server API của bạn (giữ nguyên)
        Server productionServer = new Server();
        productionServer.setUrl("https://api.logineko.edu.vn");
        productionServer.setDescription("Production server");

        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Development server");

        return new OpenAPI()
                .addServersItem(productionServer)
                .addServersItem(devServer)
                .info(new Info().title("LogiNeko").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2) // <-- Đổi type thành OAUTH2
                                .description("OAuth2 flow")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow() // <-- Chọn luồng Authorization Code
                                                // URL để chuyển hướng người dùng đến trang đăng nhập của Keycloak
                                                .authorizationUrl("https://auth.logineko.edu.vn/realms/LogiNeko/protocol/openid-connect/auth")
                                                // URL để Swagger UI đổi code lấy token
                                                .tokenUrl("https://auth.logineko.edu.vn/realms/LogiNeko/protocol/openid-connect/token")
                                                .scopes(new Scopes().addString("openid", "profile"))
                                        )
                                )
                        )
                );
    }

}
