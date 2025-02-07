package ch.scaille.tcwriter.server.webapi.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import ch.scaille.tcwriter.server.services.SessionManager;

public class WebSocketConnectionHandler<S extends Session> {

	private static final String SPRING_SESSION_ID_ATTR_NAME = "SPRING.SESSION.ID";

	public interface ConnectHandler {

		void handle(Session session, String tabId, String wsSessionId);

	}

	protected final SessionRepository<S> sessionRepository;

	protected final SessionManager sessionAccessor;

	public WebSocketConnectionHandler(SessionRepository<S> sessionRepository, SessionManager sessionAccessor) {
		this.sessionRepository = sessionRepository;
		this.sessionAccessor = sessionAccessor;
	}

	protected void handleSession(AbstractSubProtocolEvent event, ConnectHandler handler) {
		final var messageHeaders = event.getMessage().getHeaders();
		final var sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(messageHeaders);
		if (sessionAttributes == null) {
			return;
		}
		final var springSessionId = (String) sessionAttributes.get(SPRING_SESSION_ID_ATTR_NAME);
		final var session = sessionRepository.findById(springSessionId);
		if (session == null) {
			// session timed out
			return;
		}
		final var wsSessionId = SimpMessageHeaderAccessor.getSessionId(messageHeaders);
		final var tabId = NativeMessageHeaderAccessor.getFirstNativeHeader("tabId", messageHeaders);
		handler.handle(session, tabId, wsSessionId);
		sessionRepository.save(session);
	}

	public static class WebSocketConnectedHandler<S extends Session> extends WebSocketConnectionHandler<S>
			implements ApplicationListener<SessionConnectEvent> {

		public WebSocketConnectedHandler(SessionRepository<S> sessionRepository, SessionManager sessionAccessor) {
			super(sessionRepository, sessionAccessor);
		}

		@Override
		public void onApplicationEvent(SessionConnectEvent event) {
			handleSession(event, (session, tabId, wsSessionId) -> sessionAccessor.webSocketSessionIdOf(session, tabId)
					.set(wsSessionId));
		}

	}

	public static class WebSocketDisconnectedHandler<S extends Session> extends WebSocketConnectionHandler<S>
			implements ApplicationListener<SessionDisconnectEvent> {

		public WebSocketDisconnectedHandler(SessionRepository<S> sessionRepository, SessionManager sessionAccessor) {
			super(sessionRepository, sessionAccessor);
		}

		@Override
		public void onApplicationEvent(SessionDisconnectEvent event) {
			handleSession(event, (session, tabId, wsSessionId) -> {
				final var accessor = sessionAccessor.webSocketSessionIdOf(session, tabId);
				final var sessionWsSessionId = accessor.get();
				if (sessionWsSessionId.isPresent() && wsSessionId.equals(sessionWsSessionId.get())) {
					accessor.remove();
				}
			});
		}
	}

}
