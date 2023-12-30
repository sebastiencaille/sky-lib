package ch.scaille.tcwriter.server.webapi.service;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import ch.scaille.tcwriter.server.WebConstants;

public class WebSocketConnectionHandler<S extends Session> {

	private static final String SPRING_SESSION_ID_ATTR_NAME = "SPRING.SESSION.ID";

	private interface ConnectHandler {

		void handle(Session session, String tabId, String wsSessionId);

	}

	protected SessionRepository<S> sessionRepository;

	public WebSocketConnectionHandler(SessionRepository<S> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	protected void handleSession(AbstractSubProtocolEvent event, ConnectHandler handler) {
		final var messageHeaders = event.getMessage().getHeaders();
		final var sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(messageHeaders);
		if (sessionAttributes == null) {
			return;
		}
		final var springSessionId = (String) sessionAttributes.get(SPRING_SESSION_ID_ATTR_NAME);
		final var session = sessionRepository.findById(springSessionId);
		final var wsSessionId = SimpMessageHeaderAccessor.getSessionId(messageHeaders);
		final var tabId = NativeMessageHeaderAccessor.getFirstNativeHeader("tabId", messageHeaders);
		handler.handle(session, tabId, wsSessionId);
		sessionRepository.save(session);
	}

	public static class WebSocketConnectedHandler<S extends Session> extends WebSocketConnectionHandler<S>
			implements ApplicationListener<SessionConnectEvent> {

		public WebSocketConnectedHandler(SessionRepository<S> sessionRepository) {
			super(sessionRepository);
		}

		@Override
		public void onApplicationEvent(SessionConnectEvent event) {
			handleSession(event, (session, tabId, wsSessionId) -> {
				session.setAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION + tabId, wsSessionId);
			});
		}

	}

	public static class WebSocketDisconnectedHandler<S extends Session> extends WebSocketConnectionHandler<S>
			implements ApplicationListener<SessionDisconnectEvent> {

		public WebSocketDisconnectedHandler(SessionRepository<S> sessionRepository) {
			super(sessionRepository);
		}

		@Override
		public void onApplicationEvent(SessionDisconnectEvent event) {
			handleSession(event, (session, tabId, wsSessionId) -> {
				if (wsSessionId.equals(session.getAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION + tabId))) {
					session.removeAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION + tabId);
				}
			});
		}
	}

}
