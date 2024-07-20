package ch.scaille.tcwriter.server.services;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.session.Session;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import ch.scaille.tcwriter.server.dto.Context;
import jakarta.transaction.Transactional;

public class SessionManagerImpl implements SessionAccessor {

	private interface SessionAccessor {
		<T> Optional<T> get(String attribName);

		void set(String attribName, Object value);

		void remove(String attribName);
	}

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

	/**
	 * Allows to get/set a pre-defined attribute from/to the session
	 * 
	 * @param <T> the type of the attribute
	 */
	public static class GetSet<T> {

		private final SessionAccessor accessor;
		private final String attribName;
		private final Supplier<T> defaultValue;

		private GetSet(SessionAccessor accessor, String attribName, Supplier<T> defaultValue) {
			this.accessor = accessor;
			this.attribName = attribName;
			this.defaultValue = defaultValue;
		}

		/**
		 * Gets the value of the attribute
		 * 
		 * @return the value, or null if not present
		 */
		public Optional<T> get() {
			return accessor.get(attribName);
		}

		public T orElseGet(Supplier<T> orElse) {
			return get().orElseGet(orElse);
		}

		public T mandatory() {
			final var found = get();
			if (defaultValue == null) {
				return found.orElseThrow(() -> new IllegalStateException("Attribute " + attribName + " was not found"));
			}
			return found.orElseGet(defaultValue);
		}

		public void set(T value) {
			accessor.set(attribName, value);
		}
		
		public void remove() {
			accessor.remove(attribName);
		}

	}

	@Override
	@Transactional
	public GetSet<Context> getContext(NativeWebRequest request) {
		return new GetSet<>(new NativeWebRequestAccessor(request), "UserContext",
				ch.scaille.tcwriter.server.dto.Context::new);
	}

	private GetSet<String> webSocketSessionIdOf(SessionAccessor accessor, String tabId) {
		return new GetSet<>(accessor, "WsSocketSession_" + tabId, null);
	}

	@Override
	public GetSet<String> webSocketSessionIdOf(NativeWebRequest request, String tabId) {
		return webSocketSessionIdOf(new NativeWebRequestAccessor(request), tabId);
	}

	@Override
	public GetSet<String> webSocketSessionIdOf(Session session, String tabId) {
		return webSocketSessionIdOf(new SpringSessionAccess(session), tabId);
	}

}
