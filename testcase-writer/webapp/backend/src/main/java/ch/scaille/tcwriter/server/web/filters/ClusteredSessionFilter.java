package ch.scaille.tcwriter.server.web.filters;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ClusteredSessionFacade;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.util.helpers.Logs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClusteredSessionFilter extends OncePerRequestFilter {

	private static final String LAST_SESSION_SAVE_MS = "LAST_SESSION_SAVE";

	private static final String APP_SESSION_COOKIE = "TCWSESSIONID";

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
						final long delayPlusSafeTime = ofSeconds(expirationInSeconds).plus(Duration.ofHours(2))
								.toMillis();
						clusteredSessionService.deleteExpiredSessions(delayPlusSafeTime);
					}
				}, ofMinutes(5).toMillis(), ofMinutes(30).toMillis());
			}
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		startCleanup(request.getSession().getMaxInactiveInterval());

		final var contextBeforeCall = context.copy();
		final var backendSessionId = Arrays.stream(request.getCookies())
				.filter(c -> APP_SESSION_COOKIE.equals(c.getName()))
				.map(Cookie::getValue)
				.findFirst()
				.orElseGet(() -> {
					var sessionId = UUID.randomUUID().toString();
					var cookie = ResponseCookie.from(APP_SESSION_COOKIE, sessionId).httpOnly(true).path("/");
					response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
					return sessionId;
				});

		// Recover previous session
		var lastSave = (Long) request.getSession().getAttribute(LAST_SESSION_SAVE_MS);
		if (lastSave == null) {
			final var toRestore = clusteredSessionService.loadAndValidate(backendSessionId, l -> isExpired(request, l));
			if (toRestore != null) {
				Logs.of(ClusteredSessionFilter.class).info(() -> "Restoring context of session " + backendSessionId);
				contextFacade.merge(context, toRestore);
				lastSave = sessionUpdated(request);
			}
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			final var contextAfterCall = context;
			if (lastSave == null) {
				clusteredSessionService.save(backendSessionId, contextAfterCall);
				sessionUpdated(request);
			} else if (!contextAfterCall.differs(contextBeforeCall)) {
				clusteredSessionService.update(backendSessionId, contextAfterCall);
				sessionUpdated(request);
			} else if (System.currentTimeMillis() > ofMillis(lastSave).plus(ofMinutes(1)).toMillis()) {
				clusteredSessionService.touch(backendSessionId);
				sessionUpdated(request);
			}
		}
	}

	private Long sessionUpdated(HttpServletRequest request) {
		var lastSave = System.currentTimeMillis();
		request.getSession().setAttribute(LAST_SESSION_SAVE_MS, lastSave);
		return lastSave;
	}

	private boolean isExpired(HttpServletRequest request, long lastAccess) {
		return System.currentTimeMillis() > ofMillis(lastAccess)
				.plus(ofSeconds(request.getSession().getMaxInactiveInterval()))
				.toMillis();
	}

}
