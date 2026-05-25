package ch.scaille.tcwriter.server.services;

import org.jspecify.annotations.Nullable;
import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.dto.Context;

public interface SessionManager {

	SessionGetSet<Context> getContext(NativeWebRequest request);

	SessionGetSet<String> webSocketSessionIdOf(NativeWebRequest request, @Nullable String tabId);

	SessionGetSet<String> webSocketSessionIdOf(Session session, @Nullable String tabId);
	
}
