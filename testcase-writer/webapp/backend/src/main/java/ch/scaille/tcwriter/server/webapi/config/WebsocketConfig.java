package ch.scaille.tcwriter.server.webapi.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig<S extends Session>  extends AbstractSessionWebSocketMessageBrokerConfigurer<S> {

	private static final String FEEDBACK_QUEUE = "/feedback";

	public static final String TEST_FEEDBACK_DESTINATION = FEEDBACK_QUEUE + "/testexec";

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(FEEDBACK_QUEUE).setSelectorHeaderName("SPRING-SESSION-ID");
		config.setApplicationDestinationPrefixes("/ws");
		config.setUserDestinationPrefix("/user");
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
}