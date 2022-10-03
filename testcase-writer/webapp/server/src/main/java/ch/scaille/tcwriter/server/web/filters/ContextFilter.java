package ch.scaille.tcwriter.server.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.tcwriter.server.services.ContextService;

public class ContextFilter extends OncePerRequestFilter {

	private final ContextService contextService;

	public ContextFilter(ContextService contextService) {
		super();
		this.contextService = contextService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try (var autoClose = contextService.load(Identity.of(request))) {
			filterChain.doFilter(request, response);
		}
	}

}
