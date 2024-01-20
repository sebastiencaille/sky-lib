package ch.scaille.tcwriter.server.web.filter;

import static java.time.Duration.ofSeconds;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class TomcatOverloadDetectorFilter extends OncePerRequestFilter {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TomcatOverloadDetectorFilter.class);

	private static final Timer TIMER = new Timer(TomcatOverloadDetectorFilter.class.getName());

	private final AtomicInteger activeThreadsCount = new AtomicInteger(0);

	private int dumpIfMoreThan;

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		final var config = Optional.ofNullable(getFilterConfig());
		this.dumpIfMoreThan = config.map(c -> c.getInitParameter("dumpIfMoreThan")).map(Integer::parseInt).orElse(50);
		final var periodInMs = ofSeconds(config.map(c -> c.getInitParameter("taskPeriodInSeconds")).map(Integer::parseInt)
				.orElse(60)).toMillis();
		TIMER.schedule(new TimerTask() {

			@Override
			public void run() {
				TomcatOverloadDetectorFilter.this.dumpThreads();
			}
		}, periodInMs, periodInMs);
	}

	@Override
	public void destroy() {
		TIMER.cancel();
		TIMER.purge();
		super.destroy();
	}
	
	private void dumpThreads() {
		LOGGER.debug("Active threads: {}", activeThreadsCount.get());
		if (activeThreadsCount.get() <= dumpIfMoreThan) {
			return;
		}
		Thread.getAllStackTraces().forEach(this::dumpThread);
	}

	private void dumpThread(Thread thread, StackTraceElement[] stack) {
		if (!thread.getName().contains("-exec-")) {
			return;
		}
		final var log = new StringBuilder("\n[overload] ").append(thread.getName());
		Arrays.stream(stack).filter(this::keepElement)
				.forEach(e -> log.append("\n[overload] at ").append(e.getClassName()).append('.')
						.append(e.getMethodName()).append("(").append(e.getFileName()).append(':')
						.append(e.getLineNumber()).append(')'));
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn(log.toString());
		}
	}

	private boolean keepElement(StackTraceElement element) {
		return !element.getClassName().startsWith("java.") && //
				!element.getClassName().startsWith("jdk.") && //
				!element.getClassName().startsWith("org.apache.") && //
				!element.getMethodName().equals("invoke");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		activeThreadsCount.incrementAndGet();
		// to test: dumpThreads();
		try {
			super.doFilter(request, response, filterChain);
		} finally {
			activeThreadsCount.decrementAndGet();
		}
	}

}
