package ch.scaille.tcwriter.server.services;

import java.util.Optional;

import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import ch.scaille.tcwriter.server.dto.Context;
import jakarta.transaction.Transactional;

public class SessionManagerImpl implements SessionManager {

	private static class NativeWebRequestAccessor implements SessionAccessor {

		private final NativeWebRequest request;

		public NativeWebRequestAccessor(NativeWebRequest request) {
			this.request = request;
		}

		private Optional<NativeWebRequest> request() {
			return Optional.of(request);
		}

		@Override
		public <T> Optional<T> get(String attribName) {
			// Creates the session if needed
			return request().map(r -> (T)r.getAttribute(attribName, RequestAttributes.SCOPE_SESSION));
		}

		@Override
		public void set(String attribName, Object value) {
			request().ifPresent(s -> s.setAttribute(attribName, value, RequestAttributes.SCOPE_SESSION));
		}
		
		@Override
		public void remove(String attribName) {
			request().ifPresent(s -> s.removeAttribute(attribName, RequestAttributes.SCOPE_SESSION));
			
		}
	}

	private static class SpringSessionAccess implements SessionAccessor {

		private final Session session;

		public SpringSessionAccess(Session session) {
			this.session = session;
		}

		@Override
		public <T> Optional<T> get(String attribName) {
			return Optional.ofNullable(session.getAttribute(attribName));
		}

		@Override
		public void set(String attribName, Object value) {
			session.setAttribute(attribName, value);
		}
		
		@Override
		public void remove(String attribName) {
			session.removeAttribute(attribName);
		}

	}

	@Override
	@Transactional
	public SessionGetSet<Context> getContext(NativeWebRequest request) {
		return new SessionGetSet<>(new NativeWebRequestAccessor(request), "UserContext",
				ch.scaille.tcwriter.server.dto.Context::new);
	}

	private SessionGetSet<String> webSocketSessionIdOf(SessionAccessor accessor, String tabId) {
		return new SessionGetSet<>(accessor, "WsSocketSession_" + tabId, null);
	}

	@Override
	public SessionGetSet<String> webSocketSessionIdOf(NativeWebRequest request, String tabId) {
		return webSocketSessionIdOf(new NativeWebRequestAccessor(request), tabId);
	}

	@Override
	public SessionGetSet<String> webSocketSessionIdOf(Session session, String tabId) {
		return webSocketSessionIdOf(new SpringSessionAccess(session), tabId);
	}

}
