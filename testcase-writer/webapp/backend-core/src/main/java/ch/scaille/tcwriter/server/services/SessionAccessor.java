package ch.scaille.tcwriter.server.services;

import java.util.Optional;

import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.services.SessionManagerImpl.GetSet;

public interface SessionAccessor {

	GetSet<Context> getContext(Optional<NativeWebRequest> request);

	GetSet<String> webSocketSessionIdOf(Optional<NativeWebRequest> request, String tabId);

	GetSet<String> webSocketSessionIdOf(Session session, String tabId);
	
}
