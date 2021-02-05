package com.netcracker.odstc.logviewer.config;

import com.netcracker.odstc.logviewer.service.SecurityService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final SecurityService securityService;

    public WebsocketConfiguration(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/events");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) {
                    throw new SocketMessagingException("Accessor is null. Cant establish connection.");
                }
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
                    if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                        String token = authorizationHeaders.get(0);
                        if (securityService.validateToken(token)) {
                            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                        } else {
                            throw new SocketMessagingException("Connect message have invalid Authorization token. Connection refused.");
                        }
                    } else {
                        throw new SocketMessagingException("Connect message dont have Authorization header. Connection refused.");
                    }
                }
                return message;
            }
        });
    }

    private class SocketMessagingException extends RuntimeException {
        public SocketMessagingException(String message) {
            super(message);
        }
    }
}
