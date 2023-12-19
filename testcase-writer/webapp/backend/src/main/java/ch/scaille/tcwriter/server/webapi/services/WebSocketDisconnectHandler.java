package ch.scaille.tcwriter.server.webapi.services;

import org.springframework.context.ApplicationListener;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import ch.scaille.tcwriter.server.webapi.config.WebsocketConfig;

public class WebSocketDisconnectHandler<S> implements ApplicationListener<SessionDisconnectEvent> {

	private final SessionRepository<? extends Session> sessionRepository;

	public WebSocketDisconnectHandler(SessionRepository<? extends Session> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		WebSocketConnectHandler.handleSession(event.getMessage(), sessionRepository, (session, wsSessionId) -> {
			if (wsSessionId.equals(session.getAttribute(WebsocketConfig.WEBSOCKET_USER))) {
				session.removeAttribute(WebsocketConfig.WEBSOCKET_USER);
			}
		});
	}
}
