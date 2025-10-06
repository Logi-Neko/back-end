//package exe2.learningapp.logineko.authentication.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // Enable broker cho notifications
//        config.enableSimpleBroker("/topic", "/user");
//
//        // Prefix cho API endpoints
//        config.setApplicationDestinationPrefixes("/app");
//
//        // User-specific messages
//        config.setUserDestinationPrefix("/user");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // WebSocket endpoint
//        registry.addEndpoint("/notifications")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//    }
//}
