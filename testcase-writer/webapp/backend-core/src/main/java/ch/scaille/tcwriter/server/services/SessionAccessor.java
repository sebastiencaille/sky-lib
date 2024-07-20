package ch.scaille.tcwriter.server.services;

import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.services.SessionManagerImpl.GetSet;

public interface SessionAccessor {

	GetSet<Context> getContext(NativeWebRequest request);

	GetSet<String> webSocketSessionIdOf(NativeWebRequest request, String tabId);

	GetSet<String> webSocketSessionIdOf(Session session, String tabId);
	
}
