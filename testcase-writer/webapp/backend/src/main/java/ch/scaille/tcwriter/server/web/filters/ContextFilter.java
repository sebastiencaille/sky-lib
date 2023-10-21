package ch.scaille.tcwriter.server.web.filters;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ContextFilter extends OncePerRequestFilter {

	private final ContextFacade contextService;

	public ContextFilter(ContextFacade contextService) {
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
