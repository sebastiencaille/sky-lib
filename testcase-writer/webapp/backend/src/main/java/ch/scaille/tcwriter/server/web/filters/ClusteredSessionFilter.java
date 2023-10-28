package ch.scaille.tcwriter.server.web.filters;

import java.io.IOException;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ClusteredSessionFacade;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.util.helpers.Logs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClusteredSessionFilter extends OncePerRequestFilter {

	public static final String LAST_SESSION_SAVE_MS = "LAST_SESSION_SAVE";

	private final ClusteredSessionFacade clusteredSessionService;

	private final Context context;

	private final ContextFacade contextFacade;

	private Timer cleaner = null;

	public ClusteredSessionFilter(Context context, ClusteredSessionFacade clusteredSessionService,
			ContextFacade contextFacade) {
		this.context = context;
		this.clusteredSessionService = clusteredSessionService;
		this.contextFacade = contextFacade;
	}

	private void startCleanup(int expirationInSeconds) {
		if (this.cleaner == null) {
			synchronized (this) {
				this.cleaner = new Timer();
				this.cleaner.schedule(new TimerTask() {

					@Override
					public void run() {
						// Be careful with the daylight saving
						long delayPlusSafeTime = expirationInSeconds * 60_000 + Duration.ofHours(2).toMillis();
						clusteredSessionService.deleteExpiredSessions(delayPlusSafeTime);
					}
				}, Duration.ofMinutes(5).toMillis(), Duration.ofMinutes(30).toMillis());
			}
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		startCleanup(request.getSession().getMaxInactiveInterval());

		final var contextBeforeCall = context.copy();
		final var httpSession = request.getSession();

		// Recover previous session
		final var oldSessionId = request.getRequestedSessionId();
		if (!Strings.isEmpty(oldSessionId) && !httpSession.getId().equals(oldSessionId)) {
			final var toRestore = clusteredSessionService.loadAndValidate(oldSessionId, l -> isExpired(request, l));
			if (toRestore != null) {
				Logs.of(ClusteredSessionFilter.class).info("Restoring context of session " + oldSessionId);
				contextFacade.merge(context, toRestore);
			}
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			final var contextAfterCall = context;
			var lastSave = (Long) httpSession.getAttribute(LAST_SESSION_SAVE_MS);
			if (httpSession.isNew()) {
				clusteredSessionService.save(httpSession.getId(), contextAfterCall);
				sessionUpdated(request);
			} else if (!contextAfterCall.differs(contextBeforeCall)) {
				clusteredSessionService.update(httpSession.getId(), contextAfterCall);
				sessionUpdated(request);
			} else if (lastSave != null && System.currentTimeMillis() - lastSave > 60_000) {
				clusteredSessionService.touch(httpSession.getId());
				sessionUpdated(request);
			}
		}
	}

	private void sessionUpdated(HttpServletRequest request) {
		request.getSession().setAttribute(LAST_SESSION_SAVE_MS, System.currentTimeMillis());
	}

	private boolean isExpired(HttpServletRequest request, Long lastAccess) {
		return System.currentTimeMillis()
				- request.getSession().getLastAccessedTime() > request.getSession().getMaxInactiveInterval() * 60_000;
	}

}
