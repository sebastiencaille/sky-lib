package ch.scaille.tcwriter.server.webapi.services;

import java.util.function.BiConsumer;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import ch.scaille.tcwriter.server.webapi.config.WebsocketConfig;

public class WebSocketConnectHandler<S> implements ApplicationListener<SessionConnectEvent> {

	private static final String SPRING_SESSION_ID_ATTR_NAME = "SPRING.SESSION.ID";

	private SessionRepository<? extends Session> sessionRepository;

	public WebSocketConnectHandler(SessionRepository<? extends Session> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		handleSession(event.getMessage(), sessionRepository,
				(session, wsSessionId) -> session.setAttribute(WebsocketConfig.WEBSOCKET_USER, wsSessionId));
	}

	static <S extends Session> void handleSession(Message<?> message, SessionRepository<S> sessionRepository,
			BiConsumer<Session, String> sessionAction) {
		final var messageHeaders = message.getHeaders();
		final var sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(messageHeaders);
		if (sessionAttributes == null) {
			return;
		}
		final var springSessionId = (String) sessionAttributes.get(SPRING_SESSION_ID_ATTR_NAME);
		final var session = sessionRepository.findById(springSessionId);
		final var wsSessionId = SimpMessageHeaderAccessor.getSessionId(messageHeaders);
		sessionAction.accept(session, wsSessionId);
		sessionRepository.save(session);
	}
}
