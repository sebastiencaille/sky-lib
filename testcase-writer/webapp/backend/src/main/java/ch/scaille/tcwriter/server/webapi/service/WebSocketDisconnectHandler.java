package ch.scaille.tcwriter.server.webapi.service;

import org.springframework.context.ApplicationListener;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import ch.scaille.tcwriter.server.WebConstants;
import ch.scaille.util.helpers.Logs;

public class WebSocketDisconnectHandler implements ApplicationListener<SessionDisconnectEvent> {

	private final SessionRepository<? extends Session> sessionRepository;

	public WebSocketDisconnectHandler(SessionRepository<? extends Session> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		WebSocketConnectHandler.handleSession(event.getMessage(), sessionRepository, (session, wsSessionId) -> {
			if (wsSessionId.equals(session.getAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION))) {
				Logs.of(WebSocketConnectHandler.class).info("Disconnected: " + wsSessionId);
				session.removeAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION);
			}
		});
	}
}
