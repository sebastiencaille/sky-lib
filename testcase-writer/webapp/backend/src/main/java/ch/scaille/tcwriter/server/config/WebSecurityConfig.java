package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.web.filters.ContextFilter;
import ch.scaille.tcwriter.server.web.filters.TomcatOverloadDetectorFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter,
			ContextFilter contextFilter) {
		return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, tomcatOverloadDetectorFilter, contextFilter);
	}

	@Bean
	public TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter() {
		return new TomcatOverloadDetectorFilter();
	}

	@Bean
	public ContextFilter contextFilter(ContextService contextService) {
		return new ContextFilter(contextService);
	}

}