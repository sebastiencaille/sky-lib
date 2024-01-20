package ch.scaille.tcwriter.server.webapi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import ch.scaille.tcwriter.server.WebConstants;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.webapi.service.WebSocketConnectionHandler.WebSocketConnectedHandler;
import ch.scaille.tcwriter.server.webapi.service.WebSocketConnectionHandler.WebSocketDisconnectedHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {
    
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(WebConstants.TEST_EXECUTION_FEEDBACK);
		config.setApplicationDestinationPrefixes("/ws");
		config.setUserDestinationPrefix("/user");
		config.setPreservePublishOrder(true);
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		messageConverters.add(new MappingJackson2MessageConverter());
		return false;
	}

	@Override
	protected void configureStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket").setAllowedOrigins("http://localhost:9000").withSockJS();
	}
	
	@Bean
	<S extends Session> WebSocketConnectedHandler<S> webSocketConnectHandler(SessionRepository<S> sessionRepository, SessionAccessor sessionAccessor) {
		return new WebSocketConnectedHandler<>(sessionRepository, sessionAccessor);
	}

	@Bean
	<S extends Session> WebSocketDisconnectedHandler<S> webSocketDisconnectHandler(SessionRepository<S> sessionRepository, SessionAccessor sessionAccessor) {
		return new WebSocketDisconnectedHandler<>(sessionRepository, sessionAccessor);
	}

}