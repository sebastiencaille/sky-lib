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

import ch.scaille.tcwriter.server.webapi.services.WebSocketConnectHandler;
import ch.scaille.tcwriter.server.webapi.services.WebSocketDisconnectHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig<S extends Session>  extends AbstractSessionWebSocketMessageBrokerConfigurer<S> {


	private static final String QUEUE = "/queue";

	public static final String TEST_FEEDBACK_DESTINATION = QUEUE + "/testexec";

	public static final String WEBSOCKET_USER = "WebSocketUser";
    
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(TEST_FEEDBACK_DESTINATION);
		config.setApplicationDestinationPrefixes("/ws");
		//config.setUserDestinationPrefix("/user");
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
	public WebSocketConnectHandler<S> webSocketConnectHandler(SessionRepository<? extends Session> sessionRepository) {
		return new WebSocketConnectHandler<>(sessionRepository);
	}

	@Bean
	public WebSocketDisconnectHandler<S> webSocketDisconnectHandler(SessionRepository<? extends Session> sessionRepository) {
		return new WebSocketDisconnectHandler<>(sessionRepository);
	}

}