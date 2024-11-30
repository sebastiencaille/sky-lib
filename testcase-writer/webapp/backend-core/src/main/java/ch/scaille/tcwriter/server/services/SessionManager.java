package ch.scaille.tcwriter.server.services;

import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.dto.Context;

public interface SessionManager {

	SessionGetSet<Context> getContext(NativeWebRequest request);

	SessionGetSet<String> webSocketSessionIdOf(NativeWebRequest request, String tabId);

	SessionGetSet<String> webSocketSessionIdOf(Session session, String tabId);
	
}
